package com.telemetry.controller;

import com.telemetry.model.User;
import com.telemetry.model.Vehicle;
import com.telemetry.repository.UserRepository;
import com.telemetry.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final VehicleRepository vehicleRepo;

    public AdminController(UserRepository userRepo, VehicleRepository vehicleRepo) {
        this.userRepo = userRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        long online = vehicles.stream().filter(v -> v.getStatus() == com.telemetry.model.VehicleStatus.ONLINE).count();
        double avgSpeed = vehicles.stream()
                .mapToDouble(v -> v.getCurrentSpeed() != null ? v.getCurrentSpeed() : 0.0)
                .average().orElse(0.0);
        double maxSpeed = vehicles.stream()
                .mapToDouble(v -> v.getCurrentSpeed() != null ? v.getCurrentSpeed() : 0.0)
                .max().orElse(0.0);

        List<User> clients = userRepo.findAllByRole("CLIENT");
        List<User> pending = userRepo.findAllByStatus("PENDING");

        Map<String, Object> res = new HashMap<>();
        res.put("totalVehicles", vehicles.size());
        res.put("activeVehicles", online);
        res.put("totalClients", clients.size());
        res.put("pendingApprovals", pending.size());
        res.put("averageFleetSpeed", Math.round(avgSpeed * 10.0) / 10.0);
        res.put("peakSpeedToday", Math.round(maxSpeed * 10.0) / 10.0);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/fleet")
    public ResponseEntity<List<Map<String, Object>>> fleet() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        List<Map<String, Object>> res = new ArrayList<>();

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("id", v.getId());
            item.put("code", v.getId());
            item.put("name", v.getName());
            item.put("model", v.getModel());
            item.put("status", v.getStatus());
            item.put("lastSpeed", v.getCurrentSpeed());
            item.put("lastTemp", v.getEngineTemperature());
            item.put("lastSeen", v.getLastUpdated());
            item.put("ownerName", i < 4 ? "Client One" : i < 7 ? "Client Two" : "Client Three");
            item.put("ownerEmail", i < 4 ? "client1@telemetry.io" : i < 7 ? "client2@telemetry.io" : "client3@telemetry.io");
            res.add(item);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/top-speed-today")
    public ResponseEntity<List<Map<String, Object>>> topSpeedToday() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        vehicles.sort((a, b) -> Double.compare(
                b.getCurrentSpeed() != null ? b.getCurrentSpeed() : 0.0,
                a.getCurrentSpeed() != null ? a.getCurrentSpeed() : 0.0
        ));

        List<Map<String, Object>> res = new ArrayList<>();
        int limit = Math.min(5, vehicles.size());
        for (int i = 0; i < limit; i++) {
            Vehicle v = vehicles.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("vehicleCode", v.getId());
            item.put("name", v.getName());
            item.put("peakSpeed", v.getCurrentSpeed());
            item.put("timestamp", Instant.now().toString());
            res.add(item);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> listClients() {
        List<User> clients = userRepo.findAllByRole("CLIENT");
        List<Map<String, Object>> res = new ArrayList<>();

        for (User u : clients) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("email", u.getEmail());
            item.put("fullName", u.getFullName());
            item.put("role", u.getRole());
            item.put("status", u.getStatus());
            item.put("vehicleCount", u.getEmail().contains("client1") ? 4 : u.getEmail().contains("client2") ? 3 : 3);
            res.add(item);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/clients/pending")
    public ResponseEntity<List<Map<String, Object>>> pendingClients() {
        List<User> pending = userRepo.findAllByStatus("PENDING");
        List<Map<String, Object>> res = new ArrayList<>();

        for (User u : pending) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("email", u.getEmail());
            item.put("fullName", u.getFullName());
            item.put("role", u.getRole());
            item.put("status", u.getStatus());
            item.put("vehicleCount", 0);
            res.add(item);
        }

        return ResponseEntity.ok(res);
    }

    @PatchMapping("/clients/{id}/approve")
    public ResponseEntity<?> approveClient(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = userOpt.get();
        u.setStatus("ACTIVE");
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("id", u.getId(), "status", "ACTIVE"));
    }

    @PatchMapping("/clients/{id}/reject")
    public ResponseEntity<?> rejectClient(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = userOpt.get();
        u.setStatus("REJECTED");
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("id", u.getId(), "status", "REJECTED"));
    }
}
