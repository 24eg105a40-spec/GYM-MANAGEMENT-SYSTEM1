package com.gymmanagement.controller;

import com.gymmanagement.dto.PaymentRequestDto;
import com.gymmanagement.dto.PaymentResponseDto;
import com.gymmanagement.dto.PaymentVerificationRequest;
import com.gymmanagement.enums.PaymentStatus;
import com.gymmanagement.security.UserPrincipal;
import com.gymmanagement.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payments")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<PaymentResponseDto> recordPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PaymentRequestDto request) {
        return new ResponseEntity<>(paymentService.recordPayment(principal.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping("/api/payments/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.getMyPayments(principal.getId()));
    }

    @GetMapping("/api/admin/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/api/admin/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PatchMapping("/api/admin/payments/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> verifyPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.verifyPayment(id, PaymentStatus.VERIFIED, principal.getId()));
    }

    @PatchMapping("/api/admin/payments/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(id, request.getStatus(), principal.getId()));
    }
}
