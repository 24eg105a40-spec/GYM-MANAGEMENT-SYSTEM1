package com.gymmanagement.service;

import com.gymmanagement.dto.FitnessProgressRequest;
import com.gymmanagement.dto.FitnessProgressResponse;
import com.gymmanagement.entity.FitnessProgress;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.FitnessProgressRepository;
import com.gymmanagement.repository.TrainerAssignmentRepository;
import com.gymmanagement.repository.TrainerProfileRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FitnessProgressService {

    private final FitnessProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerAssignmentRepository assignmentRepository;

    public FitnessProgressService(
            FitnessProgressRepository progressRepository,
            UserRepository userRepository,
            TrainerProfileRepository trainerProfileRepository,
            TrainerAssignmentRepository assignmentRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public FitnessProgressResponse recordProgress(Long memberId, FitnessProgressRequest request) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        FitnessProgress progress = new FitnessProgress(
                member,
                request.getMeasurementDate(),
                request.getWeightKg(),
                request.getBodyFatPercentage(),
                request.getWaistCm(),
                request.getNotes()
        );

        FitnessProgress saved = progressRepository.save(progress);
        return FitnessProgressResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FitnessProgressResponse> getMyProgress(Long memberId) {
        return progressRepository.findByMemberIdOrderByMeasurementDateAsc(memberId).stream()
                .map(FitnessProgressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public FitnessProgressResponse updateProgress(Long id, Long memberId, FitnessProgressRequest request) {
        FitnessProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress record not found with id: " + id));

        if (!progress.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("You cannot edit another member's progress record");
        }

        progress.setMeasurementDate(request.getMeasurementDate());
        progress.setWeightKg(request.getWeightKg());
        progress.setBodyFatPercentage(request.getBodyFatPercentage());
        progress.setWaistCm(request.getWaistCm());
        progress.setNotes(request.getNotes());

        FitnessProgress updated = progressRepository.save(progress);
        return FitnessProgressResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteProgress(Long id, Long memberId) {
        FitnessProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress record not found with id: " + id));

        if (!progress.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("You cannot delete another member's progress record");
        }

        progressRepository.delete(progress);
    }

    @Transactional(readOnly = true)
    public List<FitnessProgressResponse> getMemberProgressForTrainer(Long memberId, Long trainerUserId) {
        TrainerProfile trainer = trainerProfileRepository.findByUserId(trainerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found for user: " + trainerUserId));

        boolean isAssigned = assignmentRepository.existsByMemberIdAndTrainerIdAndActive(memberId, trainer.getId(), true);
        if (!isAssigned) {
            throw new ForbiddenException("Access denied: You are not assigned to this member");
        }

        return progressRepository.findByMemberIdOrderByMeasurementDateAsc(memberId).stream()
                .map(FitnessProgressResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
