package com.gymmanagement.dto;

public class SessionCancellationRequest {

    private String reason;

    public SessionCancellationRequest() {
    }

    public SessionCancellationRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
