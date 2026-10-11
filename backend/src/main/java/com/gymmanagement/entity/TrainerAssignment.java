package com.gymmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_assignments", indexes = {
    @Index(name = "idx_assign_member", columnList = "member_id"),
    @Index(name = "idx_assign_trainer", columnList = "trainer_profile_id")
})
public class TrainerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_profile_id", nullable = false)
    private TrainerProfile trainer;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public TrainerAssignment() {
    }

    public TrainerAssignment(User member, TrainerProfile trainer, String notes) {
        this.member = member;
        this.trainer = trainer;
        this.notes = notes;
        this.assignedAt = LocalDateTime.now();
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
