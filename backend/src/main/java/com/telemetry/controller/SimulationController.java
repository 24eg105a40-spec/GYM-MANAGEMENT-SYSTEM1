package com.telemetry.controller;

import com.telemetry.dto.SimulationStatusDTO;
import com.telemetry.service.SimulationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;

    @Value("${telemetry.simulation.interval-ms:4000}")
    private long intervalMs;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    /**
     * GET /api/simulation/status
     * Returns whether simulation is running and current tick count.
     */
    @GetMapping("/status")
    public ResponseEntity<SimulationStatusDTO> getStatus() {
        boolean running = simulationService.isRunning();
        long ticks = simulationService.getTickCount();
        String message = running ? "Simulation engine is actively ticking." : "Simulation engine is paused.";
        return ResponseEntity.ok(new SimulationStatusDTO(running, intervalMs, ticks, message));
    }

    /**
     * POST /api/simulation/toggle
     * Pauses or resumes the telemetry simulation engine.
     */
    @PostMapping("/toggle")
    public ResponseEntity<SimulationStatusDTO> toggleSimulation() {
        boolean running = simulationService.toggleSimulation();
        long ticks = simulationService.getTickCount();
        String message = running ? "Simulation resumed." : "Simulation paused.";
        return ResponseEntity.ok(new SimulationStatusDTO(running, intervalMs, ticks, message));
    }

    /**
     * POST /api/simulation/tick
     * Triggers an immediate simulation cycle on demand.
     */
    @PostMapping("/tick")
    public ResponseEntity<SimulationStatusDTO> manualTick() {
        simulationService.performTick();
        boolean running = simulationService.isRunning();
        long ticks = simulationService.getTickCount();
        return ResponseEntity.ok(new SimulationStatusDTO(running, intervalMs, ticks, "Manual simulation tick triggered successfully."));
    }
}
