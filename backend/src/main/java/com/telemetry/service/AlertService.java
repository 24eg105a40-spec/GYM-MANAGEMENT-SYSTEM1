package com.telemetry.service;

import com.telemetry.model.Alert;
import com.telemetry.model.AlertSeverity;
import com.telemetry.model.AlertStatus;
import com.telemetry.model.AlertType;
import com.telemetry.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getAllAlerts(AlertStatus status, AlertSeverity severity, String vehicleId) {
        String cleanVehicleId = (vehicleId != null && !vehicleId.trim().isEmpty() && !vehicleId.equalsIgnoreCase("ALL"))
                ? vehicleId.trim() : null;
        return alertRepository.searchAlerts(status, severity, cleanVehicleId);
    }

    public Optional<Alert> getAlertById(Long id) {
        return alertRepository.findById(id);
    }

    public long countActiveAlerts() {
        return alertRepository.countByStatus(AlertStatus.ACTIVE);
    }

    @Transactional
    public Optional<Alert> acknowledgeAlert(Long id) {
        return alertRepository.findById(id).map(alert -> {
            if (alert.getStatus() == AlertStatus.ACTIVE) {
                alert.setStatus(AlertStatus.ACKNOWLEDGED);
                alert.setAcknowledgedAt(LocalDateTime.now());
                return alertRepository.save(alert);
            }
            return alert;
        });
    }

    @Transactional
    public Optional<Alert> resolveAlert(Long id) {
        return alertRepository.findById(id).map(alert -> {
            alert.setStatus(AlertStatus.RESOLVED);
            alert.setResolvedAt(LocalDateTime.now());
            return alertRepository.save(alert);
        });
    }

    @Transactional
    public Alert createAlertIfNotExists(String vehicleId, String vehicleName, AlertType type,
                                        AlertSeverity severity, String message,
                                        Double triggerVal, Double thresholdVal) {
        // Avoid spamming duplicate ACTIVE alerts for the exact same vehicle & type
        boolean alreadyActive = alertRepository.existsByVehicleIdAndAlertTypeAndStatus(vehicleId, type, AlertStatus.ACTIVE);
        if (!alreadyActive) {
            Alert alert = new Alert(vehicleId, vehicleName, type, severity, message, triggerVal, thresholdVal);
            return alertRepository.save(alert);
        }
        return null;
    }
}
