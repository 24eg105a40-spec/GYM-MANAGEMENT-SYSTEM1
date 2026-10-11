package com.gymmanagement.controller;

import com.gymmanagement.dto.AdminDashboardStatsDto;
import com.gymmanagement.dto.MemberDashboardStatsDto;
import com.gymmanagement.dto.TrainerDashboardStatsDto;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final AdminDashboardService dashboardService;

    public DashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardStatsDto> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
    }

    @GetMapping("/members/me/dashboard")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberDashboardStatsDto> getMemberDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dashboardService.getMemberDashboardStats(principal.getId()));
    }

    @GetMapping("/trainers/me/dashboard")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerDashboardStatsDto> getTrainerDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dashboardService.getTrainerDashboardStats(principal.getId()));
    }
}
