package com.telemetry.controller;

import com.telemetry.model.TelemetryRecord;
import com.telemetry.model.Vehicle;
import com.telemetry.model.VehicleStatus;
import com.telemetry.service.TelemetryService;
import com.telemetry.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;
    private final TelemetryService telemetryService;

    public VehicleController(VehicleService vehicleService, TelemetryService telemetryService) {
        this.vehicleService = vehicleService;
        this.telemetryService = telemetryService;
    }

    /**
     * GET /api/vehicles
     * Returns list of all vehicles with optional search query and status filter.
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(query, status));
    }

    /**
     * GET /api/vehicles/{id}
     * Returns details for a single vehicle by vehicle ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/vehicles/{id}/telemetry
     * Returns time-series telemetry data points for rendering Recharts graphs.
     */
    @GetMapping("/{id}/telemetry")
    public ResponseEntity<List<TelemetryRecord>> getVehicleTelemetry(
            @PathVariable String id,
            @RequestParam(defaultValue = "40") int limit) {
        if (vehicleService.getVehicleById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(telemetryService.getRecentTelemetry(id, limit));
    }

    /**
     * PUT /api/vehicles/{id}/status
     * Updates a vehicle status (e.g. toggle to OFFLINE or MAINTENANCE).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Vehicle> updateVehicleStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            VehicleStatus newStatus = VehicleStatus.valueOf(statusStr.toUpperCase());
            return vehicleService.updateStatus(id, newStatus)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
