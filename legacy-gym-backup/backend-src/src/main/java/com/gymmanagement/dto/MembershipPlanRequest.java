package com.gymmanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class MembershipPlanRequest {

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    private String name;

    private String description;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private int durationDays;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    private String facilities;

    private boolean includesPersonalTraining = false;

    private boolean active = true;

    public MembershipPlanRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public boolean isIncludesPersonalTraining() {
        return includesPersonalTraining;
    }

    public void setIncludesPersonalTraining(boolean includesPersonalTraining) {
        this.includesPersonalTraining = includesPersonalTraining;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
