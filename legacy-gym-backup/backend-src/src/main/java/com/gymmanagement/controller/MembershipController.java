package com.gymmanagement.controller;

import com.gymmanagement.dto.MembershipRequestDto;
import com.gymmanagement.dto.MembershipResponseDto;
import com.gymmanagement.dto.MembershipStatusUpdateRequest;
import com.gymmanagement.enums.Role;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/api/memberships")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponseDto> createMembership(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MembershipRequestDto request) {
        return new ResponseEntity<>(membershipService.createMembershipRequest(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/memberships/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<MembershipResponseDto>> getMyMemberships(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(membershipService.getMyMemberships(principal.getId()));
    }

    @GetMapping("/api/memberships/{id}")
    public ResponseEntity<MembershipResponseDto> getMembershipById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        return ResponseEntity.ok(membershipService.getMembershipById(id, principal.getId(), isAdmin));
    }

    @PostMapping("/api/memberships/{id}/renew")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponseDto> renewMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(membershipService.renewMembership(id, principal.getId()), HttpStatus.CREATED);
    }

    @PatchMapping("/api/memberships/{id}/cancel")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponseDto> cancelMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(membershipService.cancelMembership(id, principal.getId()));
    }

    @GetMapping("/api/admin/memberships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MembershipResponseDto>> getAllMemberships() {
        return ResponseEntity.ok(membershipService.getAllMemberships());
    }

    @PatchMapping("/api/admin/memberships/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponseDto> activateMembership(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.activateMembership(id));
    }

    @PatchMapping("/api/admin/memberships/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponseDto> updateMembershipStatus(
            @PathVariable Long id,
            @Valid @RequestBody MembershipStatusUpdateRequest request) {
        return ResponseEntity.ok(membershipService.updateMembershipStatus(id, request.getStatus()));
    }
}
