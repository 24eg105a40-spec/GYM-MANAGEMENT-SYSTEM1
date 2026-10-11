package com.gymmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_profile_id", nullable = false)
    private TrainerProfile trainer;

    @Column(nullable = false, length = 150)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("exerciseOrder ASC")
    private List<WorkoutExercise> exercises = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public WorkoutPlan() {
    }

    public WorkoutPlan(User member, TrainerProfile trainer, String planName, String description, LocalDate startDate, LocalDate endDate) {
        this.member = member;
        this.trainer = trainer;
        this.planName = planName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addExercise(WorkoutExercise exercise) {
        exercises.add(exercise);
        exercise.setWorkoutPlan(this);
    }

    public void removeExercise(WorkoutExercise exercise) {
        exercises.remove(exercise);
        exercise.setWorkoutPlan(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public TrainerProfile getTrainer() {
        return trainer;
    }

    public void setTrainer(TrainerProfile trainer) {
        this.trainer = trainer;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
