package com.gymmanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class WorkoutPlanRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotBlank(message = "Workout plan name is required")
    private String planName;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Valid
    private List<WorkoutExerciseRequest> exercises;

    public WorkoutPlanRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<WorkoutExerciseRequest> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseRequest> exercises) {
        this.exercises = exercises;
    }
}
