package com.gymmanagement.dto;

import com.gymmanagement.entity.TrainerAvailability;
import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerAvailabilityResponse {

    private Long id;
    private Long trainerId;
    private String trainerName;
    private LocalDate availabilityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public TrainerAvailabilityResponse() {
    }

    public static TrainerAvailabilityResponse fromEntity(TrainerAvailability availability) {
        TrainerAvailabilityResponse dto = new TrainerAvailabilityResponse();
        dto.setId(availability.getId());
        if (availability.getTrainer() != null) {
            dto.setTrainerId(availability.getTrainer().getId());
            if (availability.getTrainer().getUser() != null) {
                dto.setTrainerName(availability.getTrainer().getUser().getFullName());
            }
        }
        dto.setAvailabilityDate(availability.getAvailabilityDate());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setAvailable(availability.isAvailable());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
