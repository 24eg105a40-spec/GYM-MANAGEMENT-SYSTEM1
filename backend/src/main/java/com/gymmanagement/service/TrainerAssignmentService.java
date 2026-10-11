package com.gymmanagement.service;

import com.gymmanagement.dto.MemberSummaryDto;
import com.gymmanagement.dto.TrainerAssignmentRequest;
import com.gymmanagement.dto.TrainerAssignmentResponse;
import com.gymmanagement.dto.TrainerProfileResponse;
import com.gymmanagement.entity.TrainerAssignment;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.TrainerAssignmentRepository;
import com.gymmanagement.repository.TrainerProfileRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerAssignmentService {

    private final TrainerAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;

    public TrainerAssignmentService(
            TrainerAssignmentRepository assignmentRepository,
            UserRepository userRepository,
            TrainerProfileRepository trainerProfileRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.trainerProfileRepository = trainerProfileRepository;
    }

    @Transactional
    public TrainerAssignmentResponse assignTrainer(TrainerAssignmentRequest request) {
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        TrainerProfile trainer = trainerProfileRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found with id: " + request.getTrainerId()));

        if (!trainer.isActive()) {
            throw new BadRequestException("Cannot assign an inactive trainer");
        }

        // Deactivate any existing active assignment for this member
        Optional<TrainerAssignment> existing = assignmentRepository.findByMemberIdAndActive(request.getMemberId(), true);
        existing.ifPresent(a -> {
            a.setActive(false);
            assignmentRepository.save(a);
        });

        TrainerAssignment assignment = new TrainerAssignment(member, trainer, request.getNotes());
        TrainerAssignment saved = assignmentRepository.save(assignment);
        return TrainerAssignmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Optional<TrainerProfileResponse> getMemberAssignedTrainer(Long memberId) {
        return assignmentRepository.findByMemberIdAndActive(memberId, true)
                .map(a -> TrainerProfileResponse.fromEntity(a.getTrainer()));
    }

    @Transactional(readOnly = true)
    public List<MemberSummaryDto> getTrainerAssignedMembers(Long trainerUserId) {
        return assignmentRepository.findByTrainerUserIdAndActive(trainerUserId, true).stream()
                .map(a -> MemberSummaryDto.fromEntity(a.getMember()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainerAssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(TrainerAssignmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainerAssignmentResponse updateAssignmentStatus(Long id, boolean active) {
        TrainerAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        assignment.setActive(active);
        TrainerAssignment saved = assignmentRepository.save(assignment);
        return TrainerAssignmentResponse.fromEntity(saved);
    }
}
