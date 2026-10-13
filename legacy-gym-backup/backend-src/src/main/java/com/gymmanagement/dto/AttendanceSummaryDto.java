package com.gymmanagement.dto;

public class AttendanceSummaryDto {

    private boolean checkedInToday;
    private long totalAttendance;
    private long last30DaysAttendance;
    private long currentMonthAttendance;

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(boolean checkedInToday, long totalAttendance, long last30DaysAttendance, long currentMonthAttendance) {
        this.checkedInToday = checkedInToday;
        this.totalAttendance = totalAttendance;
        this.last30DaysAttendance = last30DaysAttendance;
        this.currentMonthAttendance = currentMonthAttendance;
    }

    public boolean isCheckedInToday() {
        return checkedInToday;
    }

    public void setCheckedInToday(boolean checkedInToday) {
        this.checkedInToday = checkedInToday;
    }

    public long getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(long totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public long getLast30DaysAttendance() {
        return last30DaysAttendance;
    }

    public void setLast30DaysAttendance(long last30DaysAttendance) {
        this.last30DaysAttendance = last30DaysAttendance;
    }

    public long getCurrentMonthAttendance() {
        return currentMonthAttendance;
    }

    public void setCurrentMonthAttendance(long currentMonthAttendance) {
        this.currentMonthAttendance = currentMonthAttendance;
    }
}
