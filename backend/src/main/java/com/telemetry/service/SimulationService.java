package com.telemetry.service;

import com.telemetry.model.*;
import com.telemetry.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service responsible for running realistic periodically simulated vehicle telemetry.
 * Generates dynamic physics-inspired drift for speed, engine temperature, battery/fuel,
 * GPS coordinates, and triggers automated threshold alerts.
 * NOTE: All data is clearly labelled as SIMULATED DATA.
 */
@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
    private final Random random = new Random();

    private final VehicleRepository vehicleRepository;
    private final TelemetryService telemetryService;
    private final AlertService alertService;

    @Value("${telemetry.threshold.overspeed-kmh:105.0}")
    private double overspeedThreshold;

    @Value("${telemetry.threshold.high-temperature-c:95.0}")
    private double highTempThreshold;

    @Value("${telemetry.threshold.low-battery-percent:15.0}")
    private double lowBatteryThreshold;

    private final AtomicBoolean isSimulationRunning = new AtomicBoolean(true);
    private final AtomicLong tickCounter = new AtomicLong(0);

    public SimulationService(VehicleRepository vehicleRepository,
                             TelemetryService telemetryService,
                             AlertService alertService) {
        this.vehicleRepository = vehicleRepository;
        this.telemetryService = telemetryService;
        this.alertService = alertService;
    }

    public boolean isRunning() {
        return isSimulationRunning.get();
    }

    public boolean toggleSimulation() {
        boolean newState = !isSimulationRunning.get();
        isSimulationRunning.set(newState);
        log.info("Vehicle Telemetry Simulation status toggled: {}", newState ? "ACTIVE" : "PAUSED");
        return newState;
    }

    public void setRunning(boolean running) {
        isSimulationRunning.set(running);
    }

    public long getTickCount() {
        return tickCounter.get();
    }

    /**
     * Periodic scheduled simulation tick (defaults to every 4000ms).
     */
    @Scheduled(fixedDelayString = "${telemetry.simulation.interval-ms:4000}")
    public void scheduledTick() {
        if (!isSimulationRunning.get()) {
            return;
        }
        performTick();
    }

    /**
     * Executes one complete simulation cycle across all vehicles.
     * Can also be called on-demand via REST endpoint.
     */
    @Transactional
    public synchronized void performTick() {
        tickCounter.incrementAndGet();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Vehicle vehicle : vehicles) {
            // Offline or maintenance vehicles remain stationary
            if (vehicle.getStatus() == VehicleStatus.OFFLINE) {
                // Ensure speed is 0 and cooling towards ambient 25°C
                double curTemp = vehicle.getEngineTemperature() != null ? vehicle.getEngineTemperature() : 25.0;
                vehicle.setCurrentSpeed(0.0);
                vehicle.setEngineTemperature(Math.max(25.0, Math.round((curTemp - 0.2) * 10.0) / 10.0));
                vehicle.setLastUpdated(now);
                vehicleRepository.save(vehicle);
                continue;
            }

            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
                vehicle.setCurrentSpeed(0.0);
                vehicle.setLastUpdated(now);
                vehicleRepository.save(vehicle);
                continue;
            }

            // --- Online or Warning Vehicle Telemetry Physics Simulation ---

            // 1. Speed Drift
            double speed = vehicle.getCurrentSpeed() != null ? vehicle.getCurrentSpeed() : 60.0;
            double speedDelta = (random.nextDouble() * 10.0) - 5.0; // -5 to +5 km/h
            speed = Math.max(10.0, Math.min(125.0, speed + speedDelta));
            speed = Math.round(speed * 10.0) / 10.0;
            vehicle.setCurrentSpeed(speed);

            // 2. Engine Temperature Drift
            double temp = vehicle.getEngineTemperature() != null ? vehicle.getEngineTemperature() : 75.0;
            double targetTemp = 70.0 + (speed / 125.0) * 28.0; // 70C at low speed up to 98C at max speed
            double tempDelta = (targetTemp - temp) * 0.08 + (random.nextDouble() * 1.0 - 0.5);
            temp = Math.max(40.0, Math.min(112.0, temp + tempDelta));
            temp = Math.round(temp * 10.0) / 10.0;
            vehicle.setEngineTemperature(temp);

            // 3. Battery / Fuel Level Consumption
            double battery = vehicle.getFuelBatteryLevel() != null ? vehicle.getFuelBatteryLevel() : 80.0;
            double consumption = 0.03 + (speed / 120.0) * 0.07; // ~0.05% drop per tick
            battery = battery - consumption;
            if (battery <= 3.0) {
                // Auto simulated recharge / refuel reset when depleted
                battery = 98.0;
            }
            battery = Math.round(battery * 10.0) / 10.0;
            vehicle.setFuelBatteryLevel(battery);

            // 4. GPS Coordinate Progression along Heading
            double lat = vehicle.getLatitude() != null ? vehicle.getLatitude() : 37.7749;
            double lon = vehicle.getLongitude() != null ? vehicle.getLongitude() : -122.4194;
            double heading = vehicle.getHeading() != null ? vehicle.getHeading() : 90.0;

            // Small random turn
            heading = (heading + (random.nextDouble() * 6.0 - 3.0) + 360.0) % 360.0;
            vehicle.setHeading(Math.round(heading * 10.0) / 10.0);

            // Calculate movement (approx 4 seconds at current km/h)
            double distanceKm = (speed * 4.0) / 3600.0;
            double rad = Math.toRadians(heading);
            double deltaLat = (distanceKm / 111.0) * Math.cos(rad);
            double deltaLon = (distanceKm / (111.0 * Math.cos(Math.toRadians(lat)))) * Math.sin(rad);

            lat = Math.round((lat + deltaLat) * 100000.0) / 100000.0;
            lon = Math.round((lon + deltaLon) * 100000.0) / 100000.0;
            vehicle.setLatitude(lat);
            vehicle.setLongitude(lon);

            // 5. Odometer update
            double odo = vehicle.getOdometerKm() != null ? vehicle.getOdometerKm() : 10000.0;
            odo = Math.round((odo + distanceKm) * 10.0) / 10.0;
            vehicle.setOdometerKm(odo);

            // 6. Health Score & Diagnostic Status
            String diagStatus = "OK";
            int health = 100;

            if (temp > highTempThreshold) {
                diagStatus = "OVERHEAT_RISK";
                health -= 25;
            }
            if (speed > overspeedThreshold) {
                diagStatus = "OVERSPEED_WARNING";
                health -= 15;
            }
            if (battery < lowBatteryThreshold) {
                diagStatus = "LOW_POWER";
                health -= 20;
            }
            vehicle.setHealthScore(Math.max(30, health));
            vehicle.setLastUpdated(now);

            // 7. Status update (auto warning if critical threshold hit)
            if (temp > highTempThreshold || speed > overspeedThreshold) {
                vehicle.setStatus(VehicleStatus.WARNING);
            } else if (vehicle.getStatus() == VehicleStatus.WARNING && temp < highTempThreshold - 3.0 && speed < overspeedThreshold - 5.0) {
                vehicle.setStatus(VehicleStatus.ONLINE);
            }

            vehicleRepository.save(vehicle);

            // 8. Record Telemetry Snapshot
            int rpm = (int) Math.round(speed * 35.0 + random.nextInt(200));
            double voltage = (vehicle.getFuelType().equalsIgnoreCase("Electric")) ? (380.0 + (battery / 100.0) * 35.0) : 13.8;
            double tirePsi = vehicle.getTirePressurePsi() != null ? vehicle.getTirePressurePsi() : 35.0;

            TelemetryRecord record = new TelemetryRecord(
                    vehicle.getId(),
                    now,
                    speed,
                    temp,
                    battery,
                    lat,
                    lon,
                    rpm,
                    Math.round(voltage * 10.0) / 10.0,
                    tirePsi,
                    diagStatus
            );
            telemetryService.recordTelemetry(record);

            // 9. Automated Threshold Alert Generation
            checkAndTriggerAlerts(vehicle, speed, temp, battery);
        }
    }

    private void checkAndTriggerAlerts(Vehicle vehicle, double speed, double temp, double battery) {
        if (speed > overspeedThreshold) {
            alertService.createAlertIfNotExists(
                    vehicle.getId(),
                    vehicle.getName(),
                    AlertType.OVERSPEED,
                    AlertSeverity.WARNING,
                    String.format("Vehicle %s exceeded speed threshold at %.1f km/h (Limit: %.1f km/h)",
                            vehicle.getId(), speed, overspeedThreshold),
                    speed,
                    overspeedThreshold
            );
        }

        if (temp > highTempThreshold) {
            alertService.createAlertIfNotExists(
                    vehicle.getId(),
                    vehicle.getName(),
                    AlertType.HIGH_TEMPERATURE,
                    AlertSeverity.CRITICAL,
                    String.format("Engine overheating detected on %s: %.1f °C (Threshold: %.1f °C)",
                            vehicle.getId(), temp, highTempThreshold),
                    temp,
                    highTempThreshold
            );
        }

        if (battery < lowBatteryThreshold) {
            alertService.createAlertIfNotExists(
                    vehicle.getId(),
                    vehicle.getName(),
                    AlertType.LOW_BATTERY_FUEL,
                    AlertSeverity.WARNING,
                    String.format("Low %s warning on %s: %.1f%% remaining (Minimum: %.1f%%)",
                            vehicle.getFuelType(), vehicle.getId(), battery, lowBatteryThreshold),
                    battery,
                    lowBatteryThreshold
            );
        }
    }
}
