package com.gymmanagement.dto;

import com.gymmanagement.entity.WorkoutExercise;
import java.math.BigDecimal;

public class WorkoutExerciseResponse {

    private Long id;
    private String exerciseName;
    private String targetMuscleGroup;
    private int sets;
    private int repetitions;
    private int restSeconds;
    private String instructions;
    private BigDecimal targetWeight;
    private int exerciseOrder;

    public WorkoutExerciseResponse() {
    }

    public static WorkoutExerciseResponse fromEntity(WorkoutExercise exercise) {
        WorkoutExerciseResponse dto = new WorkoutExerciseResponse();
        dto.setId(exercise.getId());
        dto.setExerciseName(exercise.getExerciseName());
        dto.setTargetMuscleGroup(exercise.getTargetMuscleGroup());
        dto.setSets(exercise.getSets());
        dto.setRepetitions(exercise.getRepetitions());
        dto.setRestSeconds(exercise.getRestSeconds());
        dto.setInstructions(exercise.getInstructions());
        dto.setTargetWeight(exercise.getTargetWeight());
        dto.setExerciseOrder(exercise.getExerciseOrder());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
