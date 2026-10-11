package com.gymmanagement.dto;

import com.gymmanagement.entity.MembershipPlan;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPlanResponse {

    private Long id;
    private String name;
    private String description;
    private int durationDays;
    private BigDecimal price;
    private String facilities;
    private boolean includesPersonalTraining;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipPlanResponse() {
    }

    public static MembershipPlanResponse fromEntity(MembershipPlan plan) {
        MembershipPlanResponse response = new MembershipPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setDurationDays(plan.getDurationDays());
        response.setPrice(plan.getPrice());
        response.setFacilities(plan.getFacilities());
        response.setIncludesPersonalTraining(plan.isIncludesPersonalTraining());
        response.setActive(plan.isActive());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
