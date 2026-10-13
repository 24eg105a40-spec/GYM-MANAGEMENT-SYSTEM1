-- ============================================================
-- Connected Vehicle Telemetry System
-- MySQL Initial Database Creation Script (Reference DDL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS telemetry_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE telemetry_db;

-- Note: Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update`
-- will automatically create all tables and indexes.
-- The following DDL statements are provided for reference or manual execution.

-- 1. vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ONLINE',
    license_plate VARCHAR(32),
    fuel_type VARCHAR(32) DEFAULT 'Electric',
    current_speed DOUBLE DEFAULT 0.0,
    engine_temperature DOUBLE DEFAULT 75.0,
    fuel_battery_level DOUBLE DEFAULT 85.0,
    latitude DOUBLE DEFAULT 37.7749,
    longitude DOUBLE DEFAULT -122.4194,
    heading DOUBLE DEFAULT 90.0,
    odometer_km DOUBLE DEFAULT 12450.0,
    tire_pressure_psi DOUBLE DEFAULT 35.0,
    health_score INT DEFAULT 98,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vehicle_status (status)
);

-- 2. telemetry_records table (Time-Series sensor readings)
CREATE TABLE IF NOT EXISTS telemetry_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id VARCHAR(32) NOT NULL,
    timestamp DATETIME NOT NULL,
    speed DOUBLE,
    engine_temperature DOUBLE,
    fuel_battery_level DOUBLE,
    latitude DOUBLE,
    longitude DOUBLE,
    rpm INT,
    battery_voltage DOUBLE,
    tire_pressure DOUBLE,
    diagnostic_status VARCHAR(64) DEFAULT 'OK',
    INDEX idx_telemetry_vehicle (vehicle_id),
    INDEX idx_telemetry_timestamp (timestamp),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- 3. alerts table (Threshold violation events)
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id VARCHAR(32) NOT NULL,
    vehicle_name VARCHAR(255) NOT NULL,
    alert_type VARCHAR(64) NOT NULL,
    severity VARCHAR(32) NOT NULL DEFAULT 'WARNING',
    message TEXT NOT NULL,
    trigger_value DOUBLE,
    threshold_value DOUBLE,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at DATETIME,
    resolved_at DATETIME,
    INDEX idx_alert_status (status),
    INDEX idx_alert_vehicle (vehicle_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);
