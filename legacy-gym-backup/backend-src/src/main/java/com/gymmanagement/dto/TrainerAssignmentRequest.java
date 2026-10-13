package com.gymmanagement.dto;

import com.gymmanagement.entity.TrainerAssignment;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TrainerAssignmentRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Trainer profile ID is required")
    private Long trainerId;

    private String notes;

    public TrainerAssignmentRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
