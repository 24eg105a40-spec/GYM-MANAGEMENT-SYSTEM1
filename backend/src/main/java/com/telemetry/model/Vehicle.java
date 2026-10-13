package com.telemetry.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a Connected Vehicle in the fleet.
 * Tracks hardware identification, telemetry status, real-time parameters,
 * and geographical coordinates.
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "id", length = 32, nullable = false, unique = true)
    private String id; // e.g. "VH-101"

    @Column(name = "name", nullable = false)
    private String name; // e.g. "Tesla Model 3 Fleet #1"

    @Column(name = "model", nullable = false)
    private String model; // e.g. "Model 3 Long Range"

    @Column(name = "type", nullable = false)
    private String type; // e.g. "Electric Sedan", "Electric Truck", "Cargo Van"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status = VehicleStatus.ONLINE;

    @Column(name = "license_plate", length = 32)
    private String licensePlate;

    @Column(name = "fuel_type", length = 32)
    private String fuelType = "Electric"; // "Electric", "Diesel", "Hybrid"

    @Column(name = "current_speed")
    private Double currentSpeed = 0.0; // in km/h

    @Column(name = "engine_temperature")
    private Double engineTemperature = 75.0; // in Celsius

    @Column(name = "fuel_battery_level")
    private Double fuelBatteryLevel = 85.0; // in %

    @Column(name = "latitude")
    private Double latitude = 37.7749;

    @Column(name = "longitude")
    private Double longitude = -122.4194;

    @Column(name = "heading")
    private Double heading = 90.0; // Compass heading in degrees (0 - 360)

    @Column(name = "odometer_km")
    private Double odometerKm = 12450.0;

    @Column(name = "tire_pressure_psi")
    private Double tirePressurePsi = 35.0;

    @Column(name = "health_score")
    private Integer healthScore = 98; // 0 - 100%

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(String id, String name, String model, String type, VehicleStatus status,
                   String licensePlate, String fuelType, Double currentSpeed,
                   Double engineTemperature, Double fuelBatteryLevel,
                   Double latitude, Double longitude, Double heading,
                   Double odometerKm, Double tirePressurePsi, Integer healthScore) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.type = type;
        this.status = status;
        this.licensePlate = licensePlate;
        this.fuelType = fuelType;
        this.currentSpeed = currentSpeed;
        this.engineTemperature = engineTemperature;
        this.fuelBatteryLevel = fuelBatteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.odometerKm = odometerKm;
        this.tirePressurePsi = tirePressurePsi;
        this.healthScore = healthScore;
        this.lastUpdated = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Double currentSpeed) {
        this.currentSpeed = currentSpeed;
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

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(Double odometerKm) {
        this.odometerKm = odometerKm;
    }

    public Double getTirePressurePsi() {
        return tirePressurePsi;
    }

    public void setTirePressurePsi(Double tirePressurePsi) {
        this.tirePressurePsi = tirePressurePsi;
    }

    public Integer getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
