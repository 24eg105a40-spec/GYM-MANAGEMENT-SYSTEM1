package com.telemetry.repository;

import com.telemetry.model.TelemetryRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {

    List<TelemetryRecord> findByVehicleIdOrderByTimestampAsc(String vehicleId);

    List<TelemetryRecord> findTop60ByVehicleIdOrderByTimestampDesc(String vehicleId);

    List<TelemetryRecord> findAllByOrderByTimestampDesc(Pageable pageable);

    @Query("SELECT t FROM TelemetryRecord t WHERE " +
           "(:vehicleId IS NULL OR t.vehicleId = :vehicleId) " +
           "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
           "ORDER BY t.timestamp DESC")
    List<TelemetryRecord> searchHistory(@Param("vehicleId") String vehicleId,
                                        @Param("startDate") LocalDateTime startDate,
                                        Pageable pageable);

    long countByVehicleId(String vehicleId);
}
