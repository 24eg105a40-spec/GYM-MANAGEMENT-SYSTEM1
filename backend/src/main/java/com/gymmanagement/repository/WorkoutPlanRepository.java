package com.gymmanagement.repository;

import com.gymmanagement.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByMemberIdOrderByStartDateDesc(Long memberId);
    List<WorkoutPlan> findByMemberIdAndActiveOrderByStartDateDesc(Long memberId, boolean active);
    List<WorkoutPlan> findByTrainerIdOrderByStartDateDesc(Long trainerId);
    List<WorkoutPlan> findByTrainerUserIdOrderByStartDateDesc(Long trainerUserId);
    Optional<WorkoutPlan> findFirstByMemberIdAndActiveOrderByStartDateDesc(Long memberId, boolean active);
}
