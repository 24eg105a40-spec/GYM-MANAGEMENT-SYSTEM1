package com.gymmanagement.dto;

import com.gymmanagement.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class PaymentVerificationRequest {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    public PaymentVerificationRequest() {
    }

    public PaymentVerificationRequest(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
