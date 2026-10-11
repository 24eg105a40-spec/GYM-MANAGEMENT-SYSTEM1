package com.gymmanagement.service;

import com.gymmanagement.dto.MemberSummaryDto;
import com.gymmanagement.dto.TrainerCreateRequest;
import com.gymmanagement.dto.TrainerProfileResponse;
import com.gymmanagement.dto.TrainerUpdateRequest;
import com.gymmanagement.entity.TrainerAssignment;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.Role;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.TrainerAssignmentRepository;
import com.gymmanagement.repository.TrainerProfileRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final UserRepository userRepository;
    private final TrainerAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(
            TrainerProfileRepository trainerProfileRepository,
            UserRepository userRepository,
            TrainerAssignmentRepository assignmentRepository,
            PasswordEncoder passwordEncoder) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<TrainerProfileResponse> getAllTrainers(boolean activeOnly) {
        List<TrainerProfile> list = activeOnly
                ? trainerProfileRepository.findByActive(true)
                : trainerProfileRepository.findAll();

        return list.stream()
                .map(TrainerProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrainerProfileResponse getTrainerById(Long id) {
        TrainerProfile profile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found with id: " + id));
        return TrainerProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public TrainerProfileResponse getTrainerByUserId(Long userId) {
        TrainerProfile profile = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found for user: " + userId));
        return TrainerProfileResponse.fromEntity(profile);
    }

    @Transactional
    public TrainerProfileResponse createTrainer(TrainerCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                Role.TRAINER
        );
        user = userRepository.save(user);

        TrainerProfile profile = new TrainerProfile(
                user,
                request.getSpecialization(),
                request.getQualification(),
                request.getExperienceYears(),
                request.getBio(),
                request.getHourlyRate()
        );
        profile = trainerProfileRepository.save(profile);

        return TrainerProfileResponse.fromEntity(profile);
    }

    @Transactional
    public TrainerProfileResponse updateTrainer(Long id, TrainerUpdateRequest request) {
        TrainerProfile profile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found with id: " + id));

        if (request.getFullName() != null && profile.getUser() != null) {
            profile.getUser().setFullName(request.getFullName());
        }
        if (request.getPhone() != null && profile.getUser() != null) {
            profile.getUser().setPhone(request.getPhone());
        }

        profile.setSpecialization(request.getSpecialization());
        profile.setQualification(request.getQualification());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setBio(request.getBio());
        if (request.getHourlyRate() != null) {
            profile.setHourlyRate(request.getHourlyRate());
        }

        TrainerProfile updated = trainerProfileRepository.save(profile);
        return TrainerProfileResponse.fromEntity(updated);
    }

    @Transactional
    public TrainerProfileResponse updateTrainerStatus(Long id, boolean active) {
        TrainerProfile profile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found with id: " + id));
        profile.setActive(active);
        if (profile.getUser() != null) {
            profile.getUser().setActive(active);
        }
        return TrainerProfileResponse.fromEntity(trainerProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public List<MemberSummaryDto> getAssignedMembersForTrainer(Long trainerUserId) {
        List<TrainerAssignment> assignments = assignmentRepository.findByTrainerUserIdAndActive(trainerUserId, true);
        return assignments.stream()
                .map(a -> MemberSummaryDto.fromEntity(a.getMember()))
                .collect(Collectors.toList());
    }
}
