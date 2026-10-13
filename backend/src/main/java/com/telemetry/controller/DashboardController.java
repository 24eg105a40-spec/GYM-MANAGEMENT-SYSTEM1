package com.telemetry.controller;

import com.telemetry.dto.DashboardSummaryDTO;
import com.telemetry.model.VehicleStatus;
import com.telemetry.service.AlertService;
import com.telemetry.service.SimulationService;
import com.telemetry.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final VehicleService vehicleService;
    private final AlertService alertService;
    private final SimulationService simulationService;

    public DashboardController(VehicleService vehicleService,
                               AlertService alertService,
                               SimulationService simulationService) {
        this.vehicleService = vehicleService;
        this.alertService = alertService;
        this.simulationService = simulationService;
    }

    /**
     * GET /api/dashboard/summary
     * Computes real-time fleet overview KPIs for the dashboard cards.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        long total = vehicleService.countTotal();
        long online = vehicleService.countByStatus(VehicleStatus.ONLINE);
        long offline = vehicleService.countByStatus(VehicleStatus.OFFLINE);
        long warning = vehicleService.countByStatus(VehicleStatus.WARNING);
        long maintenance = vehicleService.countByStatus(VehicleStatus.MAINTENANCE);
        long activeAlerts = alertService.countActiveAlerts();
        double avgSpeed = vehicleService.getAverageSpeed();
        double avgBattery = vehicleService.getAverageBattery();
        double fleetHealth = vehicleService.getAverageHealthScore();
        boolean isRunning = simulationService.isRunning();

        DashboardSummaryDTO summary = new DashboardSummaryDTO(
                total, online, offline, warning, maintenance,
                activeAlerts, avgSpeed, avgBattery, fleetHealth, isRunning
        );
        return ResponseEntity.ok(summary);
    }
}
