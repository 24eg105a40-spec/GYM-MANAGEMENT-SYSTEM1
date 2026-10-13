package com.gymmanagement.controller;

import com.gymmanagement.dto.MembershipResponseDto;
import com.gymmanagement.dto.UserProfileUpdateRequest;
import com.gymmanagement.dto.UserResponse;
import com.gymmanagement.enums.Role;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/members/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(memberService.getMemberProfile(principal.getId()));
    }

    @PutMapping("/api/members/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(memberService.updateMemberProfile(principal.getId(), request));
    }

    @GetMapping({"/api/admin/members", "/api/admin/users"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllMembers(
            @RequestParam(name = "role", required = false) Role role) {
        return ResponseEntity.ok(memberService.getAllMembers(role));
    }

    @GetMapping({"/api/admin/members/{id}", "/api/admin/users/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @RequestMapping(
            value = {"/api/admin/members/{id}/status", "/api/admin/users/{id}/status"},
            method = {RequestMethod.PATCH, RequestMethod.PUT}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateMemberStatus(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> statusMap,
            @RequestParam(name = "active", required = false) Boolean activeParam) {
        boolean active = true;
        if (activeParam != null) {
            active = activeParam;
        } else if (statusMap != null && statusMap.containsKey("active")) {
            active = statusMap.get("active");
        }
        return ResponseEntity.ok(memberService.updateMemberStatus(id, active));
    }

    @DeleteMapping({"/api/admin/members/{id}", "/api/admin/users/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null && principal.getId().equals(id)) {
            throw new BadRequestException("Administrator cannot delete their own active account");
        }
        memberService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User #" + id + " was deleted successfully"));
    }

    @GetMapping("/api/admin/members/{id}/membership-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MembershipResponseDto>> getMemberMembershipHistory(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberMembershipHistory(id));
    }
}
