package com.gymmanagement.repository;

import com.gymmanagement.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    List<WorkoutExercise> findByWorkoutPlanIdOrderByExerciseOrderAsc(Long workoutPlanId);
}
