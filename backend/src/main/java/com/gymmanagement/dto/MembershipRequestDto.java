package com.gymmanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MembershipRequestDto {

    @NotNull(message = "Membership plan ID is required")
    private Long planId;

    // Optional preferred start date; defaults to today or seamlessly continuous if renewal
    private LocalDate startDate;

    public MembershipRequestDto() {
    }

    public MembershipRequestDto(Long planId, LocalDate startDate) {
        this.planId = planId;
        this.startDate = startDate;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
