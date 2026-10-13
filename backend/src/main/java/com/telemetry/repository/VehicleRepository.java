package com.telemetry.repository;

import com.telemetry.model.Vehicle;
import com.telemetry.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findByStatus(VehicleStatus status);

    long countByStatus(VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:query IS NULL OR LOWER(v.id) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:status IS NULL OR v.status = :status) " +
           "ORDER BY v.id ASC")
    List<Vehicle> searchVehicles(@Param("query") String query, @Param("status") VehicleStatus status);

    @Query("SELECT AVG(v.currentSpeed) FROM Vehicle v WHERE v.status = 'ONLINE'")
    Double getAverageSpeedOfOnlineVehicles();

    @Query("SELECT AVG(v.fuelBatteryLevel) FROM Vehicle v")
    Double getAverageFuelBatteryLevel();

    @Query("SELECT AVG(v.healthScore) FROM Vehicle v")
    Double getAverageHealthScore();
}
