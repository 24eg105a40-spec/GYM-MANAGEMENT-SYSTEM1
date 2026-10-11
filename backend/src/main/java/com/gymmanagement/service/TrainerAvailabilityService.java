package com.gymmanagement.service;

import com.gymmanagement.dto.TrainerAvailabilityRequest;
import com.gymmanagement.dto.TrainerAvailabilityResponse;
import com.gymmanagement.entity.TrainerAvailability;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.TrainerAvailabilityRepository;
import com.gymmanagement.repository.TrainerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerAvailabilityService {

    private final TrainerAvailabilityRepository availabilityRepository;
    private final TrainerProfileRepository trainerProfileRepository;

    public TrainerAvailabilityService(
            TrainerAvailabilityRepository availabilityRepository,
            TrainerProfileRepository trainerProfileRepository) {
        this.availabilityRepository = availabilityRepository;
        this.trainerProfileRepository = trainerProfileRepository;
    }

    @Transactional
    public TrainerAvailabilityResponse createAvailability(Long trainerUserId, TrainerAvailabilityRequest request) {
        TrainerProfile trainer = trainerProfileRepository.findByUserId(trainerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found for user: " + trainerUserId));

        if (request.getAvailabilityDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot create availability slots in the past");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        TrainerAvailability availability = new TrainerAvailability(
                trainer,
                request.getAvailabilityDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        TrainerAvailability saved = availabilityRepository.save(availability);
        return TrainerAvailabilityResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainerAvailabilityResponse> getTrainerAvailability(Long trainerId) {
        return availabilityRepository.findByTrainerIdAndAvailableTrueAndAvailabilityDateGreaterThanEqualOrderByAvailabilityDateAscStartTimeAsc(
                trainerId, LocalDate.now()).stream()
                .map(TrainerAvailabilityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainerAvailabilityResponse> getMyAvailability(Long trainerUserId) {
        return availabilityRepository.findByTrainerUserIdAndAvailabilityDateGreaterThanEqualOrderByAvailabilityDateAscStartTimeAsc(
                trainerUserId, LocalDate.now()).stream()
                .map(TrainerAvailabilityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainerAvailabilityResponse updateAvailability(Long id, Long trainerUserId, TrainerAvailabilityRequest request) {
        TrainerAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found with id: " + id));

        if (!availability.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot edit another trainer's availability slot");
        }

        if (request.getAvailabilityDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot update availability to a past date");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        availability.setAvailabilityDate(request.getAvailabilityDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        TrainerAvailability saved = availabilityRepository.save(availability);
        return TrainerAvailabilityResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteAvailability(Long id, Long trainerUserId) {
        TrainerAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found with id: " + id));

        if (!availability.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot delete another trainer's availability slot");
        }

        availabilityRepository.delete(availability);
    }
}
