package com.gymmanagement.controller;

import com.gymmanagement.dto.TrainerAvailabilityRequest;
import com.gymmanagement.dto.TrainerAvailabilityResponse;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.TrainerAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrainerAvailabilityController {

    private final TrainerAvailabilityService availabilityService;

    public TrainerAvailabilityController(TrainerAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/api/trainers/availability")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerAvailabilityResponse> createAvailability(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        return new ResponseEntity<>(availabilityService.createAvailability(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/trainers/me/availability")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<TrainerAvailabilityResponse>> getMyAvailability(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(availabilityService.getMyAvailability(principal.getId()));
    }

    @GetMapping("/api/trainers/{id}/availability")
    public ResponseEntity<List<TrainerAvailabilityResponse>> getTrainerAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getTrainerAvailability(id));
    }

    @PutMapping("/api/trainers/availability/{id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerAvailabilityResponse> updateAvailability(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, principal.getId(), request));
    }

    @DeleteMapping("/api/trainers/availability/{id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        availabilityService.deleteAvailability(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
