package com.gymmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class WorkoutExerciseRequest {

    @NotBlank(message = "Exercise name is required")
    private String exerciseName;

    @NotBlank(message = "Target muscle group is required")
    private String targetMuscleGroup;

    @Positive(message = "Sets must be a positive number")
    private int sets;

    @Positive(message = "Repetitions must be a positive number")
    private int repetitions;

    @Positive(message = "Rest seconds must be a positive number")
    private int restSeconds;

    private String instructions;

    private BigDecimal targetWeight;

    @Positive(message = "Exercise order must be a positive number")
    private int exerciseOrder = 1;

    public WorkoutExerciseRequest() {
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
