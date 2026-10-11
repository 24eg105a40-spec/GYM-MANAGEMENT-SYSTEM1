package com.gymmanagement.dto;

import com.gymmanagement.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;

public class SessionStatusUpdateRequest {

    @NotNull(message = "Session status is required")
    private SessionStatus status;

    private String cancellationReason;

    public SessionStatusUpdateRequest() {
    }

    public SessionStatusUpdateRequest(SessionStatus status) {
        this.status = status;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
