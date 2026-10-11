package com.gymmanagement.dto;

import com.gymmanagement.entity.FitnessProgress;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FitnessProgressResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private LocalDate measurementDate;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPercentage;
    private BigDecimal waistCm;
    private String notes;
    private LocalDateTime createdAt;

    public FitnessProgressResponse() {
    }

    public static FitnessProgressResponse fromEntity(FitnessProgress progress) {
        FitnessProgressResponse dto = new FitnessProgressResponse();
        dto.setId(progress.getId());
        if (progress.getMember() != null) {
            dto.setMemberId(progress.getMember().getId());
            dto.setMemberName(progress.getMember().getFullName());
        }
        dto.setMeasurementDate(progress.getMeasurementDate());
        dto.setWeightKg(progress.getWeightKg());
        dto.setBodyFatPercentage(progress.getBodyFatPercentage());
        dto.setWaistCm(progress.getWaistCm());
        dto.setNotes(progress.getNotes());
        dto.setCreatedAt(progress.getCreatedAt());
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

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(BigDecimal bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public BigDecimal getWaistCm() {
        return waistCm;
    }

    public void setWaistCm(BigDecimal waistCm) {
        this.waistCm = waistCm;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
