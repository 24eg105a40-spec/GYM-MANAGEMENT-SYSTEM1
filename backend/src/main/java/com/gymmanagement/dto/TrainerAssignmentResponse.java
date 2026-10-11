package com.gymmanagement.dto;

import com.gymmanagement.entity.TrainerAssignment;
import java.time.LocalDateTime;

public class TrainerAssignmentResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long trainerId;
    private Long trainerUserId;
    private String trainerName;
    private String trainerEmail;
    private String specialization;
    private LocalDateTime assignedAt;
    private boolean active;
    private String notes;

    public TrainerAssignmentResponse() {
    }

    public static TrainerAssignmentResponse fromEntity(TrainerAssignment assignment) {
        TrainerAssignmentResponse dto = new TrainerAssignmentResponse();
        dto.setId(assignment.getId());
        if (assignment.getMember() != null) {
            dto.setMemberId(assignment.getMember().getId());
            dto.setMemberName(assignment.getMember().getFullName());
            dto.setMemberEmail(assignment.getMember().getEmail());
        }
        if (assignment.getTrainer() != null) {
            dto.setTrainerId(assignment.getTrainer().getId());
            dto.setSpecialization(assignment.getTrainer().getSpecialization());
            if (assignment.getTrainer().getUser() != null) {
                dto.setTrainerUserId(assignment.getTrainer().getUser().getId());
                dto.setTrainerName(assignment.getTrainer().getUser().getFullName());
                dto.setTrainerEmail(assignment.getTrainer().getUser().getEmail());
            }
        }
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setActive(assignment.isActive());
        dto.setNotes(assignment.getNotes());
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

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getTrainerUserId() {
        return trainerUserId;
    }

    public void setTrainerUserId(Long trainerUserId) {
        this.trainerUserId = trainerUserId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(String trainerEmail) {
        this.trainerEmail = trainerEmail;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
