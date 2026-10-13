package com.telemetry.controller;

import com.telemetry.model.Alert;
import com.telemetry.model.AlertSeverity;
import com.telemetry.model.AlertStatus;
import com.telemetry.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * GET /api/alerts
     * List alerts with optional status, severity, and vehicle filters.
     */
    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts(
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) String vehicleId) {
        return ResponseEntity.ok(alertService.getAllAlerts(status, severity, vehicleId));
    }

    /**
     * PUT /api/alerts/{id}/acknowledge
     * Acknowledge an active alert.
     */
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Alert> acknowledgeAlert(@PathVariable Long id) {
        return alertService.acknowledgeAlert(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/alerts/{id}/resolve
     * Resolve an alert.
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Alert> resolveAlert(@PathVariable Long id) {
        return alertService.resolveAlert(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
