package com.gymmanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerAvailabilityRequest {

    @NotNull(message = "Availability date is required")
    private LocalDate availabilityDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    public TrainerAvailabilityRequest() {
    }

    public LocalDate getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(LocalDate availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
