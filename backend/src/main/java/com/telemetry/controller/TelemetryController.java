package com.telemetry.controller;

import com.telemetry.model.TelemetryRecord;
import com.telemetry.service.TelemetryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    /**
     * GET /api/telemetry/history
     * Returns paginated historical sensor records with optional vehicle filter and start date.
     */
    @GetMapping("/history")
    public ResponseEntity<List<TelemetryRecord>> getTelemetryHistory(
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(telemetryService.getHistory(vehicleId, startDate, page, size));
    }

    /**
     * POST /api/telemetry
     * Manually record a single telemetry snapshot (e.g. from an external simulated sensor agent).
     */
    @PostMapping
    public ResponseEntity<TelemetryRecord> recordTelemetry(@RequestBody TelemetryRecord record) {
        TelemetryRecord saved = telemetryService.recordTelemetry(record);
        return ResponseEntity.ok(saved);
    }
}
