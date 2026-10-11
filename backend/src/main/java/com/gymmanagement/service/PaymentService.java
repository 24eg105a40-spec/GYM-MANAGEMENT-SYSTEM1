package com.gymmanagement.service;

import com.gymmanagement.dto.PaymentRequestDto;
import com.gymmanagement.dto.PaymentResponseDto;
import com.gymmanagement.entity.Membership;
import com.gymmanagement.entity.Payment;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.MembershipStatus;
import com.gymmanagement.enums.PaymentStatus;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.MembershipRepository;
import com.gymmanagement.repository.PaymentRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            MembershipRepository membershipRepository,
            UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponseDto recordPayment(Long memberId, PaymentRequestDto request) {
        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + request.getMembershipId()));

        if (!membership.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("You can only submit payments for your own memberships");
        }

        if (paymentRepository.existsByTransactionReference(request.getTransactionReference())) {
            throw new ConflictException("Transaction reference is already registered: " + request.getTransactionReference());
        }

        Payment payment = new Payment(
                membership,
                request.getAmount(),
                request.getPaymentMethod(),
                request.getTransactionReference(),
                PaymentStatus.PENDING
        );

        Payment saved = paymentRepository.save(payment);
        return PaymentResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getMyPayments(Long memberId) {
        List<Payment> list = paymentRepository.findByMembershipMemberIdOrderByPaymentDateDesc(memberId);
        return list.stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentResponseDto.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto verifyPayment(Long paymentId, PaymentStatus newStatus, Long adminId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

        payment.setStatus(newStatus);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // If payment is successfully VERIFIED, automatically activate the pending membership!
        if (newStatus == PaymentStatus.VERIFIED) {
            Membership membership = payment.getMembership();
            if (membership.getStatus() == MembershipStatus.PENDING) {
                if (membership.getStartDate().isBefore(LocalDate.now())) {
                    int days = membership.getMembershipPlan().getDurationDays();
                    membership.setStartDate(LocalDate.now());
                    membership.setEndDate(LocalDate.now().plusDays(days));
                }
                membership.setStatus(MembershipStatus.ACTIVE);
                membershipRepository.save(membership);
            }
        }

        return PaymentResponseDto.fromEntity(saved);
    }
}
