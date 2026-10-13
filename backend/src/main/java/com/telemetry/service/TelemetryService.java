package com.telemetry.service;

import com.telemetry.model.TelemetryRecord;
import com.telemetry.repository.TelemetryRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class TelemetryService {

    private final TelemetryRecordRepository telemetryRepository;

    public TelemetryService(TelemetryRecordRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public List<TelemetryRecord> getRecentTelemetry(String vehicleId, int limit) {
        List<TelemetryRecord> records = telemetryRepository.findTop60ByVehicleIdOrderByTimestampDesc(vehicleId);
        // Reverse so it is chronological (oldest to newest) for Recharts X-axis time series
        Collections.reverse(records);
        return records;
    }

    public List<TelemetryRecord> getHistory(String vehicleId, LocalDateTime startDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String cleanVehicleId = (vehicleId != null && !vehicleId.trim().isEmpty() && !vehicleId.equalsIgnoreCase("ALL"))
                ? vehicleId.trim() : null;
        return telemetryRepository.searchHistory(cleanVehicleId, startDate, pageable);
    }

    @Transactional
    public TelemetryRecord recordTelemetry(TelemetryRecord record) {
        if (record.getTimestamp() == null) {
            record.setTimestamp(LocalDateTime.now());
        }
        return telemetryRepository.save(record);
    }

    public long countByVehicle(String vehicleId) {
        return telemetryRepository.countByVehicleId(vehicleId);
    }
}
