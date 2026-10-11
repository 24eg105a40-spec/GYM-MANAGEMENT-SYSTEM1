package com.gymmanagement.service;

import com.gymmanagement.dto.TrainingSessionBookingRequest;
import com.gymmanagement.dto.TrainingSessionResponse;
import com.gymmanagement.entity.TrainerAvailability;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.TrainingSession;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.SessionStatus;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.TrainerAvailabilityRepository;
import com.gymmanagement.repository.TrainerProfileRepository;
import com.gymmanagement.repository.TrainingSessionRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public TrainingSessionService(
            TrainingSessionRepository sessionRepository,
            TrainerProfileRepository trainerProfileRepository,
            TrainerAvailabilityRepository availabilityRepository,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TrainingSessionResponse bookSession(Long memberId, TrainingSessionBookingRequest request) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        TrainerProfile trainer = trainerProfileRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found with id: " + request.getTrainerId()));

        if (!trainer.isActive()) {
            throw new BadRequestException("Selected trainer is currently inactive");
        }

        // Prevent booking in the past
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (request.getSessionDate().isBefore(today) ||
                (request.getSessionDate().isEqual(today) && request.getStartTime().isBefore(now))) {
            throw new BadRequestException("Cannot book training sessions in the past");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
            throw new BadRequestException("Session end time must be after start time");
        }

        // Prevent member from having overlapping sessions
        boolean memberOverlap = sessionRepository.existsOverlappingSessionForMember(
                memberId, request.getSessionDate(), request.getStartTime(), request.getEndTime());
        if (memberOverlap) {
            throw new ConflictException("You already have an existing session scheduled during this time slot");
        }

        // Prevent trainer from having overlapping confirmed sessions
        boolean trainerOverlap = sessionRepository.existsOverlappingConfirmedSessionForTrainer(
                trainer.getId(), request.getSessionDate(), request.getStartTime(), request.getEndTime());
        if (trainerOverlap) {
            throw new ConflictException("Trainer already has a confirmed session during this time slot");
        }

        TrainerAvailability availability = null;
        if (request.getAvailabilityId() != null) {
            availability = availabilityRepository.findById(request.getAvailabilityId()).orElse(null);
            if (availability != null) {
                if (!availability.isAvailable()) {
                    throw new ConflictException("Selected trainer availability slot is no longer open");
                }
                // Mark slot as booked
                availability.setAvailable(false);
                availabilityRepository.save(availability);
            }
        }

        TrainingSession session = new TrainingSession(
                member,
                trainer,
                availability,
                request.getSessionDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getPurpose()
        );

        TrainingSession saved = sessionRepository.save(session);
        return TrainingSessionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> getMySessions(Long memberId) {
        return sessionRepository.findByMemberIdOrderBySessionDateDescStartTimeDesc(memberId).stream()
                .map(TrainingSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> getTrainerSessions(Long trainerUserId) {
        return sessionRepository.findByTrainerUserIdOrderBySessionDateDescStartTimeDesc(trainerUserId).stream()
                .map(TrainingSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(TrainingSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrainingSessionResponse getSessionById(Long id, Long currentUserId, boolean isTrainer, boolean isAdmin) {
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));

        if (!isAdmin) {
            if (isTrainer) {
                if (!session.getTrainer().getUser().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You cannot view another trainer's session");
                }
            } else {
                if (!session.getMember().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You cannot view another member's session");
                }
            }
        }

        return TrainingSessionResponse.fromEntity(session);
    }

    @Transactional
    public TrainingSessionResponse updateSessionStatus(Long id, Long trainerUserId, SessionStatus newStatus, String cancellationReason) {
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));

        if (!session.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot modify another trainer's session");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BadRequestException("Completed sessions cannot be modified or cancelled");
        }

        // If confirming, re-verify trainer has no overlapping confirmed sessions
        if (newStatus == SessionStatus.CONFIRMED) {
            boolean overlap = sessionRepository.existsOverlappingConfirmedSessionForTrainer(
                    session.getTrainer().getId(), session.getSessionDate(), session.getStartTime(), session.getEndTime());
            if (overlap) {
                throw new ConflictException("Cannot confirm: Overlapping confirmed session exists for this time window");
            }
        }

        // Release slot if rejected or cancelled
        if ((newStatus == SessionStatus.REJECTED || newStatus == SessionStatus.CANCELLED) && session.getAvailability() != null) {
            session.getAvailability().setAvailable(true);
            availabilityRepository.save(session.getAvailability());
        }

        session.setStatus(newStatus);
        if (cancellationReason != null) {
            session.setCancellationReason(cancellationReason);
        }

        TrainingSession saved = sessionRepository.save(session);
        return TrainingSessionResponse.fromEntity(saved);
    }

    @Transactional
    public TrainingSessionResponse cancelSession(Long id, Long currentUserId, boolean isTrainer, boolean isAdmin, String reason) {
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));

        if (!isAdmin) {
            if (isTrainer) {
                if (!session.getTrainer().getUser().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You cannot cancel another trainer's session");
                }
            } else {
                if (!session.getMember().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You cannot cancel another member's session");
                }
            }
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BadRequestException("Completed sessions cannot be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        session.setCancellationReason(reason != null ? reason : "Cancelled by user");

        // Release availability slot if attached
        if (session.getAvailability() != null) {
            session.getAvailability().setAvailable(true);
            availabilityRepository.save(session.getAvailability());
        }

        TrainingSession saved = sessionRepository.save(session);
        return TrainingSessionResponse.fromEntity(saved);
    }
}
