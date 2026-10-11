package com.gymmanagement.controller;

import com.gymmanagement.dto.TrainerAssignmentRequest;
import com.gymmanagement.dto.TrainerAssignmentResponse;
import com.gymmanagement.dto.TrainerProfileResponse;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.TrainerAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TrainerAssignmentController {

    private final TrainerAssignmentService assignmentService;

    public TrainerAssignmentController(TrainerAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/api/admin/trainer-assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerAssignmentResponse> assignTrainer(@Valid @RequestBody TrainerAssignmentRequest request) {
        return new ResponseEntity<>(assignmentService.assignTrainer(request), HttpStatus.CREATED);
    }

    @GetMapping("/api/members/me/trainer")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<TrainerProfileResponse> getMyAssignedTrainer(@AuthenticationPrincipal UserPrincipal principal) {
        return assignmentService.getMemberAssignedTrainer(principal.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/api/admin/trainer-assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TrainerAssignmentResponse>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @PatchMapping("/api/admin/trainer-assignments/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerAssignmentResponse> updateAssignmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(assignmentService.updateAssignmentStatus(id, active));
    }
}
