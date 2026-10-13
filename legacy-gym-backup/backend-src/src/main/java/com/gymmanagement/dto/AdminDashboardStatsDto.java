package com.gymmanagement.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardStatsDto {

    private long totalMembers;
    private long activeMembers;
    private long expiredMemberships;
    private long pendingMemberships;
    private long totalActiveTrainers;
    private long totalPlans;
    private long todayAttendance;
    private long pendingPayments;
    private long upcomingSessions;
    private BigDecimal totalVerifiedRevenue;
    private BigDecimal monthlyVerifiedRevenue;
    private List<PaymentResponseDto> recentPayments;
    private List<MembershipResponseDto> recentMemberships;
    private List<UserResponse> recentUsers;

    public AdminDashboardStatsDto() {
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getExpiredMemberships() {
        return expiredMemberships;
    }

    public void setExpiredMemberships(long expiredMemberships) {
        this.expiredMemberships = expiredMemberships;
    }

    public long getPendingMemberships() {
        return pendingMemberships;
    }

    public void setPendingMemberships(long pendingMemberships) {
        this.pendingMemberships = pendingMemberships;
    }

    public long getTotalActiveTrainers() {
        return totalActiveTrainers;
    }

    public void setTotalActiveTrainers(long totalActiveTrainers) {
        this.totalActiveTrainers = totalActiveTrainers;
    }

    public long getTotalPlans() {
        return totalPlans;
    }

    public void setTotalPlans(long totalPlans) {
        this.totalPlans = totalPlans;
    }

    public long getTodayAttendance() {
        return todayAttendance;
    }

    public void setTodayAttendance(long todayAttendance) {
        this.todayAttendance = todayAttendance;
    }

    public long getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(long pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public long getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(long upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public BigDecimal getTotalVerifiedRevenue() {
        return totalVerifiedRevenue;
    }

    public void setTotalVerifiedRevenue(BigDecimal totalVerifiedRevenue) {
        this.totalVerifiedRevenue = totalVerifiedRevenue;
    }

    public BigDecimal getMonthlyVerifiedRevenue() {
        return monthlyVerifiedRevenue;
    }

    public void setMonthlyVerifiedRevenue(BigDecimal monthlyVerifiedRevenue) {
        this.monthlyVerifiedRevenue = monthlyVerifiedRevenue;
    }

    public List<PaymentResponseDto> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<PaymentResponseDto> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<MembershipResponseDto> getRecentMemberships() {
        return recentMemberships;
    }

    public void setRecentMemberships(List<MembershipResponseDto> recentMemberships) {
        this.recentMemberships = recentMemberships;
    }

    public List<UserResponse> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<UserResponse> recentUsers) {
        this.recentUsers = recentUsers;
    }
}
