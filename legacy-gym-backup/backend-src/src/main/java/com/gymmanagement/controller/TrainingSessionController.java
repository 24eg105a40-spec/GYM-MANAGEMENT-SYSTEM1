package com.gymmanagement.controller;

import com.gymmanagement.dto.SessionCancellationRequest;
import com.gymmanagement.dto.SessionStatusUpdateRequest;
import com.gymmanagement.dto.TrainingSessionBookingRequest;
import com.gymmanagement.dto.TrainingSessionResponse;
import com.gymmanagement.enums.Role;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrainingSessionController {

    private final TrainingSessionService sessionService;

    public TrainingSessionController(TrainingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/api/training-sessions")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<TrainingSessionResponse> bookSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TrainingSessionBookingRequest request) {
        return new ResponseEntity<>(sessionService.bookSession(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/training-sessions/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<TrainingSessionResponse>> getMySessions(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(sessionService.getMySessions(principal.getId()));
    }

    @GetMapping("/api/trainers/me/sessions")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<TrainingSessionResponse>> getMyTrainerSessions(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(sessionService.getTrainerSessions(principal.getId()));
    }

    @GetMapping("/api/admin/training-sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TrainingSessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/api/training-sessions/{id}")
    public ResponseEntity<TrainingSessionResponse> getSessionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isTrainer = principal.getRole() == Role.TRAINER;
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        return ResponseEntity.ok(sessionService.getSessionById(id, principal.getId(), isTrainer, isAdmin));
    }

    @PatchMapping("/api/trainers/sessions/{id}/status")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainingSessionResponse> updateSessionStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SessionStatusUpdateRequest request) {
        return ResponseEntity.ok(sessionService.updateSessionStatus(id, principal.getId(), request.getStatus(), request.getCancellationReason()));
    }

    @PatchMapping("/api/training-sessions/{id}/cancel")
    public ResponseEntity<TrainingSessionResponse> cancelSession(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) SessionCancellationRequest request) {
        boolean isTrainer = principal.getRole() == Role.TRAINER;
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        String reason = (request != null) ? request.getReason() : "Cancelled by user";
        return ResponseEntity.ok(sessionService.cancelSession(id, principal.getId(), isTrainer, isAdmin, reason));
    }
}
