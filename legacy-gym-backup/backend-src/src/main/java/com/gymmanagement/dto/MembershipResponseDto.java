package com.gymmanagement.dto;

import com.gymmanagement.entity.Membership;
import com.gymmanagement.enums.MembershipStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MembershipResponseDto {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long planId;
    private String planName;
    private BigDecimal planPrice;
    private int durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;
    private long remainingDays;
    private boolean isExpired;
    private LocalDateTime createdAt;

    public MembershipResponseDto() {
    }

    public static MembershipResponseDto fromEntity(Membership membership) {
        MembershipResponseDto dto = new MembershipResponseDto();
        dto.setId(membership.getId());
        if (membership.getMember() != null) {
            dto.setMemberId(membership.getMember().getId());
            dto.setMemberName(membership.getMember().getFullName());
            dto.setMemberEmail(membership.getMember().getEmail());
        }
        if (membership.getMembershipPlan() != null) {
            dto.setPlanId(membership.getMembershipPlan().getId());
            dto.setPlanName(membership.getMembershipPlan().getName());
            dto.setPlanPrice(membership.getMembershipPlan().getPrice());
            dto.setDurationDays(membership.getMembershipPlan().getDurationDays());
        }
        dto.setStartDate(membership.getStartDate());
        dto.setEndDate(membership.getEndDate());
        dto.setStatus(membership.getStatus());
        dto.setCreatedAt(membership.getCreatedAt());

        LocalDate today = LocalDate.now();
        if (membership.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(today, membership.getEndDate());
            dto.setRemainingDays(Math.max(0, days));
            dto.setExpired(today.isAfter(membership.getEndDate()));
        }

        return dto;
    }

    // Getters and Setters
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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(BigDecimal planPrice) {
        this.planPrice = planPrice;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
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

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(long remainingDays) {
        this.remainingDays = remainingDays;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
