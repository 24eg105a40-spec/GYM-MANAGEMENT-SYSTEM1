package com.telemetry.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a discrete Telemetry Snapshot recorded from vehicle sensors.
 * Provides the data points rendered by Recharts in the frontend.
 */
@Entity
@Table(name = "telemetry_records")
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false, length = 32)
    private String vehicleId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "speed")
    private Double speed; // km/h

    @Column(name = "engine_temperature")
    private Double engineTemperature; // Celsius

    @Column(name = "fuel_battery_level")
    private Double fuelBatteryLevel; // %

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "rpm")
    private Integer rpm; // Engine/Motor Revolutions Per Minute

    @Column(name = "battery_voltage")
    private Double batteryVoltage; // Volts (e.g. 13.8V or 398V pack)

    @Column(name = "tire_pressure")
    private Double tirePressure; // PSI

    @Column(name = "diagnostic_status")
    private String diagnosticStatus = "OK"; // "OK", "OVERHEAT_RISK", "OVERSPEED_WARNING", "LOW_POWER"

    public TelemetryRecord() {}

    public TelemetryRecord(String vehicleId, LocalDateTime timestamp, Double speed,
                           Double engineTemperature, Double fuelBatteryLevel,
                           Double latitude, Double longitude, Integer rpm,
                           Double batteryVoltage, Double tirePressure, String diagnosticStatus) {
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
        this.speed = speed;
        this.engineTemperature = engineTemperature;
        this.fuelBatteryLevel = fuelBatteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rpm = rpm;
        this.batteryVoltage = batteryVoltage;
        this.tirePressure = tirePressure;
        this.diagnosticStatus = diagnosticStatus;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getEngineTemperature() {
        return engineTemperature;
    }

    public void setEngineTemperature(Double engineTemperature) {
        this.engineTemperature = engineTemperature;
    }

    public Double getFuelBatteryLevel() {
        return fuelBatteryLevel;
    }

    public void setFuelBatteryLevel(Double fuelBatteryLevel) {
        this.fuelBatteryLevel = fuelBatteryLevel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Double getTirePressure() {
        return tirePressure;
    }

    public void setTirePressure(Double tirePressure) {
        this.tirePressure = tirePressure;
    }

    public String getDiagnosticStatus() {
        return diagnosticStatus;
    }

    public void setDiagnosticStatus(String diagnosticStatus) {
        this.diagnosticStatus = diagnosticStatus;
    }
}
