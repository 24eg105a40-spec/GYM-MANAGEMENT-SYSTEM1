package com.gymmanagement.dto;

import java.util.List;

public class MemberDashboardStatsDto {

    private MembershipResponseDto activeMembership;
    private AttendanceSummaryDto attendanceSummary;
    private TrainerProfileResponse assignedTrainer;
    private WorkoutPlanResponse currentWorkoutPlan;
    private FitnessProgressResponse latestProgress;
    private List<TrainingSessionResponse> upcomingSessions;
    private List<AttendanceResponseDto> recentAttendance;

    public MemberDashboardStatsDto() {
    }

    public MembershipResponseDto getActiveMembership() {
        return activeMembership;
    }

    public void setActiveMembership(MembershipResponseDto activeMembership) {
        this.activeMembership = activeMembership;
    }

    public AttendanceSummaryDto getAttendanceSummary() {
        return attendanceSummary;
    }

    public void setAttendanceSummary(AttendanceSummaryDto attendanceSummary) {
        this.attendanceSummary = attendanceSummary;
    }

    public TrainerProfileResponse getAssignedTrainer() {
        return assignedTrainer;
    }

    public void setAssignedTrainer(TrainerProfileResponse assignedTrainer) {
        this.assignedTrainer = assignedTrainer;
    }

    public WorkoutPlanResponse getCurrentWorkoutPlan() {
        return currentWorkoutPlan;
    }

    public void setCurrentWorkoutPlan(WorkoutPlanResponse currentWorkoutPlan) {
        this.currentWorkoutPlan = currentWorkoutPlan;
    }

    public FitnessProgressResponse getLatestProgress() {
        return latestProgress;
    }

    public void setLatestProgress(FitnessProgressResponse latestProgress) {
        this.latestProgress = latestProgress;
    }

    public List<TrainingSessionResponse> getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(List<TrainingSessionResponse> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public List<AttendanceResponseDto> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<AttendanceResponseDto> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }
}
