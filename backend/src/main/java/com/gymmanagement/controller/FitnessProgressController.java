package com.gymmanagement.controller;

import com.gymmanagement.dto.FitnessProgressRequest;
import com.gymmanagement.dto.FitnessProgressResponse;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.FitnessProgressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FitnessProgressController {

    private final FitnessProgressService progressService;

    public FitnessProgressController(FitnessProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/api/progress")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<FitnessProgressResponse> recordProgress(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FitnessProgressRequest request) {
        return new ResponseEntity<>(progressService.recordProgress(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/progress/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<FitnessProgressResponse>> getMyProgress(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getMyProgress(principal.getId()));
    }

    @PutMapping("/api/progress/{id}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<FitnessProgressResponse> updateProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FitnessProgressRequest request) {
        return ResponseEntity.ok(progressService.updateProgress(id, principal.getId(), request));
    }

    @DeleteMapping("/api/progress/{id}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Void> deleteProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        progressService.deleteProgress(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/trainers/members/{memberId}/progress")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<FitnessProgressResponse>> getMemberProgress(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getMemberProgressForTrainer(memberId, principal.getId()));
    }
}
