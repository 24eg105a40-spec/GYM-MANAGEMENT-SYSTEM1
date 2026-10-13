package com.gymmanagement.service;

import com.gymmanagement.dto.WorkoutExerciseRequest;
import com.gymmanagement.dto.WorkoutExerciseResponse;
import com.gymmanagement.dto.WorkoutPlanRequest;
import com.gymmanagement.dto.WorkoutPlanResponse;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import com.gymmanagement.entity.WorkoutExercise;
import com.gymmanagement.entity.WorkoutPlan;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutExerciseRepository exerciseRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public WorkoutPlanService(
            WorkoutPlanRepository workoutPlanRepository,
            WorkoutExerciseRepository exerciseRepository,
            TrainerProfileRepository trainerProfileRepository,
            TrainerAssignmentRepository assignmentRepository,
            UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseRepository = exerciseRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkoutPlanResponse createWorkoutPlan(Long trainerUserId, WorkoutPlanRequest request) {
        TrainerProfile trainer = trainerProfileRepository.findByUserId(trainerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found for user: " + trainerUserId));

        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        // Check that trainer is assigned to this member
        boolean isAssigned = assignmentRepository.existsByMemberIdAndTrainerIdAndActive(member.getId(), trainer.getId(), true);
        if (!isAssigned) {
            throw new ForbiddenException("You can only create workout plans for members assigned to you");
        }

        WorkoutPlan plan = new WorkoutPlan(
                member,
                trainer,
                request.getPlanName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (request.getExercises() != null) {
            for (WorkoutExerciseRequest exReq : request.getExercises()) {
                WorkoutExercise exercise = new WorkoutExercise(
                        exReq.getExerciseName(),
                        exReq.getTargetMuscleGroup(),
                        exReq.getSets(),
                        exReq.getRepetitions(),
                        exReq.getRestSeconds(),
                        exReq.getInstructions(),
                        exReq.getTargetWeight(),
                        exReq.getExerciseOrder()
                );
                plan.addExercise(exercise);
            }
        }

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        return WorkoutPlanResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> getMemberWorkoutPlans(Long memberId) {
        return workoutPlanRepository.findByMemberIdOrderByStartDateDesc(memberId).stream()
                .map(WorkoutPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> getTrainerWorkoutPlans(Long trainerUserId) {
        return workoutPlanRepository.findByTrainerUserIdOrderByStartDateDesc(trainerUserId).stream()
                .map(WorkoutPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutPlanResponse getWorkoutPlanById(Long id, Long currentUserId, boolean isTrainer, boolean isAdmin) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + id));

        if (!isAdmin) {
            if (isTrainer) {
                if (!plan.getTrainer().getUser().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You are not authorized to view another trainer's workout plan");
                }
            } else {
                if (!plan.getMember().getId().equals(currentUserId)) {
                    throw new ForbiddenException("You are not authorized to view this workout plan");
                }
            }
        }

        return WorkoutPlanResponse.fromEntity(plan);
    }

    @Transactional
    public WorkoutPlanResponse updateWorkoutPlan(Long id, Long trainerUserId, WorkoutPlanRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + id));

        if (!plan.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot modify another trainer's workout plan");
        }

        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());

        WorkoutPlan updated = workoutPlanRepository.save(plan);
        return WorkoutPlanResponse.fromEntity(updated);
    }

    @Transactional
    public WorkoutPlanResponse updateWorkoutPlanStatus(Long id, Long trainerUserId, boolean active) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + id));

        if (!plan.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot modify another trainer's workout plan");
        }

        plan.setActive(active);
        return WorkoutPlanResponse.fromEntity(workoutPlanRepository.save(plan));
    }

    @Transactional
    public WorkoutExerciseResponse addExercise(Long planId, Long trainerUserId, WorkoutExerciseRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (!plan.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot add exercises to another trainer's workout plan");
        }

        WorkoutExercise exercise = new WorkoutExercise(
                request.getExerciseName(),
                request.getTargetMuscleGroup(),
                request.getSets(),
                request.getRepetitions(),
                request.getRestSeconds(),
                request.getInstructions(),
                request.getTargetWeight(),
                request.getExerciseOrder()
        );

        plan.addExercise(exercise);
        exerciseRepository.save(exercise);
        return WorkoutExerciseResponse.fromEntity(exercise);
    }

    @Transactional
    public WorkoutExerciseResponse updateExercise(Long planId, Long exerciseId, Long trainerUserId, WorkoutExerciseRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (!plan.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot edit exercises on another trainer's workout plan");
        }

        WorkoutExercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        if (!exercise.getWorkoutPlan().getId().equals(planId)) {
            throw new ForbiddenException("Exercise does not belong to the specified plan");
        }

        exercise.setExerciseName(request.getExerciseName());
        exercise.setTargetMuscleGroup(request.getTargetMuscleGroup());
        exercise.setSets(request.getSets());
        exercise.setRepetitions(request.getRepetitions());
        exercise.setRestSeconds(request.getRestSeconds());
        exercise.setInstructions(request.getInstructions());
        exercise.setTargetWeight(request.getTargetWeight());
        exercise.setExerciseOrder(request.getExerciseOrder());

        return WorkoutExerciseResponse.fromEntity(exerciseRepository.save(exercise));
    }

    @Transactional
    public void deleteExercise(Long planId, Long exerciseId, Long trainerUserId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (!plan.getTrainer().getUser().getId().equals(trainerUserId)) {
            throw new ForbiddenException("You cannot delete exercises on another trainer's workout plan");
        }

        WorkoutExercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        plan.removeExercise(exercise);
        exerciseRepository.delete(exercise);
    }
}
