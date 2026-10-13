package com.gymmanagement.dto;

import com.gymmanagement.entity.WorkoutPlan;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutPlanResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private Long trainerId;
    private String trainerName;
    private String planName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private List<WorkoutExerciseResponse> exercises = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkoutPlanResponse() {
    }

    public static WorkoutPlanResponse fromEntity(WorkoutPlan plan) {
        WorkoutPlanResponse dto = new WorkoutPlanResponse();
        dto.setId(plan.getId());
        if (plan.getMember() != null) {
            dto.setMemberId(plan.getMember().getId());
            dto.setMemberName(plan.getMember().getFullName());
        }
        if (plan.getTrainer() != null && plan.getTrainer().getUser() != null) {
            dto.setTrainerId(plan.getTrainer().getId());
            dto.setTrainerName(plan.getTrainer().getUser().getFullName());
        }
        dto.setPlanName(plan.getPlanName());
        dto.setDescription(plan.getDescription());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setActive(plan.isActive());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());

        if (plan.getExercises() != null) {
            dto.setExercises(plan.getExercises().stream()
                    .map(WorkoutExerciseResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<WorkoutExerciseResponse> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseResponse> exercises) {
        this.exercises = exercises;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
