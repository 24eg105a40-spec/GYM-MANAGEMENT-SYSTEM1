package com.gymmanagement.dto;

import com.gymmanagement.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;

public class MembershipStatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private MembershipStatus status;

    public MembershipStatusUpdateRequest() {
    }

    public MembershipStatusUpdateRequest(MembershipStatus status) {
        this.status = status;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }
}
