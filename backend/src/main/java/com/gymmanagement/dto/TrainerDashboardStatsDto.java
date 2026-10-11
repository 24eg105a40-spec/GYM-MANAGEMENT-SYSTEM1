package com.gymmanagement.dto;

import java.util.List;

public class TrainerDashboardStatsDto {

    private long assignedMembersCount;
    private long pendingSessionsCount;
    private long upcomingSessionsCount;
    private List<TrainingSessionResponse> todaySessions;
    private List<TrainingSessionResponse> upcomingSessions;
    private List<MemberSummaryDto> assignedMembers;

    public TrainerDashboardStatsDto() {
    }

    public long getAssignedMembersCount() {
        return assignedMembersCount;
    }

    public void setAssignedMembersCount(long assignedMembersCount) {
        this.assignedMembersCount = assignedMembersCount;
    }

    public long getPendingSessionsCount() {
        return pendingSessionsCount;
    }

    public void setPendingSessionsCount(long pendingSessionsCount) {
        this.pendingSessionsCount = pendingSessionsCount;
    }

    public long getUpcomingSessionsCount() {
        return upcomingSessionsCount;
    }

    public void setUpcomingSessionsCount(long upcomingSessionsCount) {
        this.upcomingSessionsCount = upcomingSessionsCount;
    }

    public List<TrainingSessionResponse> getTodaySessions() {
        return todaySessions;
    }

    public void setTodaySessions(List<TrainingSessionResponse> todaySessions) {
        this.todaySessions = todaySessions;
    }

    public List<TrainingSessionResponse> getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(List<TrainingSessionResponse> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public List<MemberSummaryDto> getAssignedMembers() {
        return assignedMembers;
    }

    public void setAssignedMembers(List<MemberSummaryDto> assignedMembers) {
        this.assignedMembers = assignedMembers;
    }
}
