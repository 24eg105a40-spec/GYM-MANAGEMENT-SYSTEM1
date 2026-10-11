package com.gymmanagement.controller;

import com.gymmanagement.dto.MemberSummaryDto;
import com.gymmanagement.dto.TrainerCreateRequest;
import com.gymmanagement.dto.TrainerProfileResponse;
import com.gymmanagement.dto.TrainerUpdateRequest;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<TrainerProfileResponse>> getAllTrainers(
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(trainerService.getAllTrainers(activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerProfileResponse> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getTrainerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerProfileResponse> createTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        return new ResponseEntity<>(trainerService.createTrainer(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerProfileResponse> updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody TrainerUpdateRequest request) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerProfileResponse> updateTrainerStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(trainerService.updateTrainerStatus(id, active));
    }

    @GetMapping("/me/members")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<MemberSummaryDto>> getMyAssignedMembers(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainerService.getAssignedMembersForTrainer(principal.getId()));
    }

    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainerService.getTrainerByUserId(principal.getId()));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerProfileResponse currentProfile = trainerService.getTrainerByUserId(principal.getId());
        return ResponseEntity.ok(trainerService.updateTrainer(currentProfile.getId(), request));
    }
}
