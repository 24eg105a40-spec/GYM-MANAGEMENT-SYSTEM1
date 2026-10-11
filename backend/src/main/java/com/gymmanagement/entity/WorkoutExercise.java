package com.gymmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(nullable = false, length = 100)
    private String exerciseName;

    @Column(nullable = false, length = 100)
    private String targetMuscleGroup;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int repetitions;

    @Column(nullable = false)
    private int restSeconds;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(precision = 6, scale = 2)
    private BigDecimal targetWeight;

    @Column(nullable = false)
    private int exerciseOrder = 1;

    public WorkoutExercise() {
    }

    public WorkoutExercise(String exerciseName, String targetMuscleGroup, int sets, int repetitions, int restSeconds, String instructions, BigDecimal targetWeight, int exerciseOrder) {
        this.exerciseName = exerciseName;
        this.targetMuscleGroup = targetMuscleGroup;
        this.sets = sets;
        this.repetitions = repetitions;
        this.restSeconds = restSeconds;
        this.instructions = instructions;
        this.targetWeight = targetWeight;
        this.exerciseOrder = exerciseOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getTargetMuscleGroup() {
        return targetMuscleGroup;
    }

    public void setTargetMuscleGroup(String targetMuscleGroup) {
        this.targetMuscleGroup = targetMuscleGroup;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(int restSeconds) {
        this.restSeconds = restSeconds;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(BigDecimal targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(int exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }
}
