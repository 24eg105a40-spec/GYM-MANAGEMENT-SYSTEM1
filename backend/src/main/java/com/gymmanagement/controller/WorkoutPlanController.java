package com.gymmanagement.controller;

import com.gymmanagement.dto.WorkoutExerciseRequest;
import com.gymmanagement.dto.WorkoutExerciseResponse;
import com.gymmanagement.dto.WorkoutPlanRequest;
import com.gymmanagement.dto.WorkoutPlanResponse;
import com.gymmanagement.enums.Role;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.WorkoutPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PostMapping("/api/trainers/workout-plans")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutPlanRequest request) {
        return new ResponseEntity<>(workoutPlanService.createWorkoutPlan(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/members/me/workout-plans")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<WorkoutPlanResponse>> getMyWorkoutPlans(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workoutPlanService.getMemberWorkoutPlans(principal.getId()));
    }

    @GetMapping("/api/trainers/me/workout-plans")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<WorkoutPlanResponse>> getMyCreatedWorkoutPlans(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workoutPlanService.getTrainerWorkoutPlans(principal.getId()));
    }

    @GetMapping("/api/trainers/workout-plans/{id}")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlanById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isTrainer = principal.getRole() == Role.TRAINER;
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlanById(id, principal.getId(), isTrainer, isAdmin));
    }

    @PutMapping("/api/trainers/workout-plans/{id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutPlanRequest request) {
        return ResponseEntity.ok(workoutPlanService.updateWorkoutPlan(id, principal.getId(), request));
    }

    @PatchMapping("/api/trainers/workout-plans/{id}/status")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlanStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Boolean> statusMap) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(workoutPlanService.updateWorkoutPlanStatus(id, principal.getId(), active));
    }

    @PostMapping("/api/trainers/workout-plans/{id}/exercises")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<WorkoutExerciseResponse> addExercise(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutExerciseRequest request) {
        return new ResponseEntity<>(workoutPlanService.addExercise(id, principal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/api/trainers/workout-plans/{id}/exercises/{exerciseId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<WorkoutExerciseResponse> updateExercise(
            @PathVariable Long id,
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutExerciseRequest request) {
        return ResponseEntity.ok(workoutPlanService.updateExercise(id, exerciseId, principal.getId(), request));
    }

    @DeleteMapping("/api/trainers/workout-plans/{id}/exercises/{exerciseId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long id,
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        workoutPlanService.deleteExercise(id, exerciseId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
