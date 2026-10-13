package com.gymmanagement.dto;

import com.gymmanagement.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AttendanceCorrectionRequest {

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    public AttendanceCorrectionRequest() {
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
