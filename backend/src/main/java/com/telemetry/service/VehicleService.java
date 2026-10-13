package com.telemetry.service;

import com.telemetry.model.Vehicle;
import com.telemetry.model.VehicleStatus;
import com.telemetry.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles(String query, VehicleStatus status) {
        if ((query == null || query.trim().isEmpty()) && status == null) {
            return vehicleRepository.findAll();
        }
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return vehicleRepository.searchVehicles(cleanQuery, status);
    }

    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        vehicle.setLastUpdated(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Optional<Vehicle> updateStatus(String id, VehicleStatus status) {
        return vehicleRepository.findById(id).map(v -> {
            v.setStatus(status);
            if (status == VehicleStatus.OFFLINE) {
                v.setCurrentSpeed(0.0);
            }
            v.setLastUpdated(LocalDateTime.now());
            return vehicleRepository.save(v);
        });
    }

    public long countTotal() {
        return vehicleRepository.count();
    }

    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.countByStatus(status);
    }

    public Double getAverageSpeed() {
        Double avg = vehicleRepository.getAverageSpeedOfOnlineVehicles();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public Double getAverageBattery() {
        Double avg = vehicleRepository.getAverageFuelBatteryLevel();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public Double getAverageHealthScore() {
        Double avg = vehicleRepository.getAverageHealthScore();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 100.0;
    }
}
