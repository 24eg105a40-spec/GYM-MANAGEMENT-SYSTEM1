package com.gymmanagement.dto;

import com.gymmanagement.entity.Payment;
import com.gymmanagement.enums.PaymentMethod;
import com.gymmanagement.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {

    private Long id;
    private Long membershipId;
    private String planName;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String verifiedByName;
    private LocalDateTime verifiedAt;

    public PaymentResponseDto() {
    }

    public static PaymentResponseDto fromEntity(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        if (payment.getMembership() != null) {
            dto.setMembershipId(payment.getMembership().getId());
            if (payment.getMembership().getMembershipPlan() != null) {
                dto.setPlanName(payment.getMembership().getMembershipPlan().getName());
            }
            if (payment.getMembership().getMember() != null) {
                dto.setMemberId(payment.getMembership().getMember().getId());
                dto.setMemberName(payment.getMembership().getMember().getFullName());
                dto.setMemberEmail(payment.getMembership().getMember().getEmail());
            }
        }
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        if (payment.getVerifiedBy() != null) {
            dto.setVerifiedByName(payment.getVerifiedBy().getFullName());
        }
        dto.setVerifiedAt(payment.getVerifiedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getVerifiedByName() {
        return verifiedByName;
    }

    public void setVerifiedByName(String verifiedByName) {
        this.verifiedByName = verifiedByName;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
