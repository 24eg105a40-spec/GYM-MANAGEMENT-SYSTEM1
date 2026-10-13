package com.gymmanagement.dto;

import com.gymmanagement.entity.TrainingSession;
import com.gymmanagement.enums.SessionStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TrainingSessionResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private Long trainerId;
    private Long trainerUserId;
    private String trainerName;
    private String trainerEmail;
    private String specialization;
    private Long availabilityId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private SessionStatus status;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TrainingSessionResponse() {
    }

    public static TrainingSessionResponse fromEntity(TrainingSession session) {
        TrainingSessionResponse dto = new TrainingSessionResponse();
        dto.setId(session.getId());
        if (session.getMember() != null) {
            dto.setMemberId(session.getMember().getId());
            dto.setMemberName(session.getMember().getFullName());
            dto.setMemberEmail(session.getMember().getEmail());
            dto.setMemberPhone(session.getMember().getPhone());
        }
        if (session.getTrainer() != null) {
            dto.setTrainerId(session.getTrainer().getId());
            dto.setSpecialization(session.getTrainer().getSpecialization());
            if (session.getTrainer().getUser() != null) {
                dto.setTrainerUserId(session.getTrainer().getUser().getId());
                dto.setTrainerName(session.getTrainer().getUser().getFullName());
                dto.setTrainerEmail(session.getTrainer().getUser().getEmail());
            }
        }
        if (session.getAvailability() != null) {
            dto.setAvailabilityId(session.getAvailability().getId());
        }
        dto.setSessionDate(session.getSessionDate());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setPurpose(session.getPurpose());
        dto.setStatus(session.getStatus());
        dto.setCancellationReason(session.getCancellationReason());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
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

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
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

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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
