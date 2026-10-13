package com.telemetry.service;

import com.telemetry.model.*;
import com.telemetry.repository.AlertRepository;
import com.telemetry.repository.TelemetryRecordRepository;
import com.telemetry.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeds the database on initial startup with a realistic fleet of 10 connected vehicles,
 * historical time-series telemetry data (for Recharts graphs), and active alerts.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final VehicleRepository vehicleRepository;
    private final TelemetryRecordRepository telemetryRepository;
    private final AlertRepository alertRepository;
    private final com.telemetry.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataSeeder(VehicleRepository vehicleRepository,
                      TelemetryRecordRepository telemetryRepository,
                      AlertRepository alertRepository,
                      com.telemetry.repository.UserRepository userRepository,
                      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.vehicleRepository = vehicleRepository;
        this.telemetryRepository = telemetryRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();

        if (vehicleRepository.count() > 0) {
            log.info("Database already contains vehicle records. Skipping vehicle seeding.");
            return;
        }

        log.info("Starting initial seed data population for Connected Vehicle Telemetry System...");

        List<Vehicle> vehicles = List.of(
            new Vehicle("VH-101", "Tesla Model 3 Fleet Alpha", "Model 3 Long Range", "Electric Sedan",
                    VehicleStatus.ONLINE, "CA-782-XQ", "Electric", 74.5, 76.2, 88.0,
                    37.7749, -122.4194, 45.0, 14250.0, 36.0, 98),

            new Vehicle("VH-102", "Volvo FH Electric Heavy", "FH Electric 4x2", "Electric Semi-Truck",
                    VehicleStatus.ONLINE, "OR-491-LT", "Electric", 84.0, 83.5, 62.5,
                    37.7833, -122.4167, 120.0, 38120.0, 105.0, 95),

            new Vehicle("VH-103", "Ford F-150 Lightning Pro", "Lightning Pro Dual Motor", "Fleet Pickup",
                    VehicleStatus.WARNING, "WA-902-TR", "Electric", 109.2, 98.4, 42.0,
                    37.7650, -122.4300, 260.0, 22400.0, 38.0, 72),

            new Vehicle("VH-104", "Tata Nexon EV Max Fleet", "Nexon EV Max 40.5kWh", "Compact Electric SUV",
                    VehicleStatus.ONLINE, "DL-01-AB-4021", "Electric", 52.0, 68.0, 79.5,
                    37.7580, -122.4140, 180.0, 11800.0, 33.0, 99),

            new Vehicle("VH-105", "Mercedes eSprinter Cargo", "eSprinter Van 114 High Roof", "Commercial Cargo Van",
                    VehicleStatus.ONLINE, "NV-583-VN", "Electric", 61.5, 74.0, 34.0,
                    37.7890, -122.4010, 310.0, 45300.0, 55.0, 93),

            new Vehicle("VH-106", "Scania R500 Long Haul", "R500 V8 Streamline", "Diesel Commercial Hauler",
                    VehicleStatus.OFFLINE, "TX-312-HD", "Diesel", 0.0, 25.0, 18.0,
                    37.7320, -122.3900, 0.0, 112000.0, 110.0, 88),

            new Vehicle("VH-107", "Rivian R1T Adventure", "R1T Quad-Motor AWD", "Electric Pickup",
                    VehicleStatus.ONLINE, "CO-881-RV", "Electric", 68.0, 72.5, 91.0,
                    37.7600, -122.4450, 90.0, 16900.0, 48.0, 97),

            new Vehicle("VH-108", "BYD Atto 3 Urban Service", "Atto 3 Extended Range", "Electric Crossover",
                    VehicleStatus.MAINTENANCE, "AZ-219-BD", "Electric", 0.0, 38.0, 12.0,
                    37.7450, -122.4200, 0.0, 29800.0, 35.0, 75),

            new Vehicle("VH-109", "Audi e-tron GT Express", "e-tron GT Quattro", "Performance EV",
                    VehicleStatus.ONLINE, "CA-512-GT", "Electric", 96.0, 89.0, 55.0,
                    37.7950, -122.4250, 140.0, 19400.0, 39.0, 91),

            new Vehicle("VH-110", "Hyundai Ioniq 5 Metro", "Ioniq 5 AWD Long Range", "Urban Fleet EV",
                    VehicleStatus.ONLINE, "UT-704-HY", "Electric", 48.5, 66.0, 83.0,
                    37.7700, -122.3950, 20.0, 15300.0, 36.0, 100)
        );

        vehicleRepository.saveAll(vehicles);
        log.info("Seeded {} vehicles.", vehicles.size());

        // Generate baseline historical telemetry records (25 records per vehicle)
        Random rnd = new Random(42);
        List<TelemetryRecord> historicalRecords = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(50);

        for (Vehicle v : vehicles) {
            double curSpeed = v.getCurrentSpeed();
            double curTemp = v.getEngineTemperature();
            double curBatt = v.getFuelBatteryLevel();

            for (int i = 0; i < 25; i++) {
                LocalDateTime pointTime = baseTime.plusMinutes(i * 2);
                double histSpeed = (v.getStatus() == VehicleStatus.OFFLINE || v.getStatus() == VehicleStatus.MAINTENANCE)
                        ? 0.0
                        : Math.max(10.0, Math.min(125.0, curSpeed + (rnd.nextDouble() * 16.0 - 8.0)));
                double histTemp = (v.getStatus() == VehicleStatus.OFFLINE)
                        ? 25.0
                        : Math.max(45.0, Math.min(105.0, curTemp + (rnd.nextDouble() * 4.0 - 2.0)));
                double histBatt = Math.min(100.0, Math.max(5.0, curBatt + (25 - i) * 0.15));

                String diag = (histTemp > 95.0) ? "OVERHEAT_RISK" : (histSpeed > 105.0 ? "OVERSPEED_WARNING" : "OK");

                TelemetryRecord rec = new TelemetryRecord(
                        v.getId(),
                        pointTime,
                        Math.round(histSpeed * 10.0) / 10.0,
                        Math.round(histTemp * 10.0) / 10.0,
                        Math.round(histBatt * 10.0) / 10.0,
                        v.getLatitude(),
                        v.getLongitude(),
                        (int) (histSpeed * 35),
                        388.0,
                        v.getTirePressurePsi(),
                        diag
                );
                historicalRecords.add(rec);
            }
        }

        telemetryRepository.saveAll(historicalRecords);
        log.info("Seeded {} historical telemetry snapshots for Recharts graphs.", historicalRecords.size());

        // Seed initial alerts
        List<Alert> initialAlerts = List.of(
            new Alert("VH-103", "Ford F-150 Lightning Pro", AlertType.OVERSPEED,
                    AlertSeverity.WARNING, "Vehicle VH-103 exceeded speed threshold at 109.2 km/h (Limit: 105.0 km/h)",
                    109.2, 105.0),

            new Alert("VH-103", "Ford F-150 Lightning Pro", AlertType.HIGH_TEMPERATURE,
                    AlertSeverity.CRITICAL, "Engine overheating detected on VH-103: 98.4 °C (Threshold: 95.0 °C)",
                    98.4, 95.0),

            new Alert("VH-108", "BYD Atto 3 Urban Service", AlertType.LOW_BATTERY_FUEL,
                    AlertSeverity.WARNING, "Low Electric warning on VH-108: 12.0% remaining (Minimum: 15.0%)",
                    12.0, 15.0)
        );

        alertRepository.saveAll(initialAlerts);
        log.info("Seeded {} initial alerts.", initialAlerts.size());
        log.info("Database initialization complete! Connected Vehicle Telemetry System is ready.");
    }

    private void seedUsers() {
        record UserSeed(String email, String password, String name, String role, String status) {}
        List<UserSeed> seeds = List.of(
                new UserSeed("admin@telemetry.io", "admin123", "System Administrator", "ADMIN", "ACTIVE"),
                new UserSeed("client1@telemetry.io", "client123", "Client One (Fleet)", "CLIENT", "ACTIVE"),
                new UserSeed("client2@telemetry.io", "client123", "Client Two (Logistics)", "CLIENT", "ACTIVE"),
                new UserSeed("client3@telemetry.io", "client123", "Client Three (Freight)", "CLIENT", "ACTIVE"),
                new UserSeed("admin@gmail.com", "Admin@123", "System Admin", "ADMIN", "ACTIVE"),
                new UserSeed("client1@gmail.com", "Client@123", "Client One", "CLIENT", "ACTIVE"),
                new UserSeed("operator_pending@telemetry.io", "client123", "Pending Logistics Operator", "CLIENT", "PENDING")
        );

        for (UserSeed s : seeds) {
            if (!userRepository.existsByEmail(s.email())) {
                User u = new User(
                        s.email(),
                        passwordEncoder.encode(s.password()),
                        s.name(),
                        s.role(),
                        s.status()
                );
                userRepository.save(u);
                log.info("Seeded user: {} [{}]", s.email(), s.role());
            }
        }
    }
}
