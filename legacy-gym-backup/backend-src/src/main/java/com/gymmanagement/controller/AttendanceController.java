package com.gymmanagement.controller;

import com.gymmanagement.dto.AttendanceCorrectionRequest;
import com.gymmanagement.dto.AttendanceResponseDto;
import com.gymmanagement.dto.AttendanceSummaryDto;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/attendance/check-in")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AttendanceResponseDto> checkIn(@AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(attendanceService.checkIn(principal.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/attendance/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<AttendanceResponseDto>> getMyAttendance(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attendanceService.getMyAttendance(principal.getId()));
    }

    @GetMapping("/attendance/my/summary")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AttendanceSummaryDto> getMyAttendanceSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attendanceService.getMyAttendanceSummary(principal.getId()));
    }

    @GetMapping("/admin/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceResponseDto>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAllAttendance(date));
    }

    @GetMapping("/admin/attendance/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminAttendanceSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("todayAttendanceCount", attendanceService.getTodayAttendanceCount());
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/admin/attendance/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendanceResponseDto> correctAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceCorrectionRequest request) {
        return ResponseEntity.ok(attendanceService.correctAttendance(id, request));
    }
}
