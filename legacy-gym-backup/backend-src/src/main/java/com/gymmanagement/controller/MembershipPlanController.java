package com.gymmanagement.controller;

import com.gymmanagement.dto.MembershipPlanRequest;
import com.gymmanagement.dto.MembershipPlanResponse;
import com.gymmanagement.service.MembershipPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    public MembershipPlanController(MembershipPlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<MembershipPlanResponse>> getAllPlans(
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(planService.getAllPlans(activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipPlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlanResponse> createPlan(@Valid @RequestBody MembershipPlanRequest request) {
        return new ResponseEntity<>(planService.createPlan(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody MembershipPlanRequest request) {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlanResponse> updatePlanStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(planService.updatePlanStatus(id, active));
    }
}
