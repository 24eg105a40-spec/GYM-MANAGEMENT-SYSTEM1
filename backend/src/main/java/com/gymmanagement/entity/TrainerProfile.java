package com.gymmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "trainer_profiles")
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String specialization;

    @Column(nullable = false, length = 150)
    private String qualification;

    @Column(nullable = false)
    private int experienceYears;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    public TrainerProfile() {
    }

    public TrainerProfile(User user, String specialization, String qualification, int experienceYears, String bio, BigDecimal hourlyRate) {
        this.user = user;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.bio = bio;
        this.hourlyRate = hourlyRate != null ? hourlyRate : BigDecimal.ZERO;
        this.active = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
