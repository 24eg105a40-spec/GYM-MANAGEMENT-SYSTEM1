package com.gymmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FitnessProgressRequest {

    @NotNull(message = "Measurement date is required")
    private LocalDate measurementDate;

    @NotNull(message = "Weight (kg) is required")
    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "400.0", message = "Weight must be less than 400 kg")
    private BigDecimal weightKg;

    @DecimalMin(value = "1.0", message = "Body fat percentage must be positive")
    @DecimalMax(value = "70.0", message = "Body fat percentage must be realistic")
    private BigDecimal bodyFatPercentage;

    @DecimalMin(value = "30.0", message = "Waist measurement must be at least 30 cm")
    @DecimalMax(value = "250.0", message = "Waist measurement must be realistic")
    private BigDecimal waistCm;

    private String notes;

    public FitnessProgressRequest() {
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
}
