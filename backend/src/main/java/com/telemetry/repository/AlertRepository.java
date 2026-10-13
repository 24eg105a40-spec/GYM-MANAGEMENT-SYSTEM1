package com.telemetry.repository;

import com.telemetry.model.Alert;
import com.telemetry.model.AlertSeverity;
import com.telemetry.model.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findAllByOrderByCreatedAtDesc();

    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

    List<Alert> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);

    long countByStatus(AlertStatus status);

    @Query("SELECT a FROM Alert a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "(:vehicleId IS NULL OR a.vehicleId = :vehicleId) " +
           "ORDER BY a.createdAt DESC")
    List<Alert> searchAlerts(@Param("status") AlertStatus status,
                             @Param("severity") AlertSeverity severity,
                             @Param("vehicleId") String vehicleId);

    boolean existsByVehicleIdAndAlertTypeAndStatus(String vehicleId, com.telemetry.model.AlertType alertType, AlertStatus status);
}
