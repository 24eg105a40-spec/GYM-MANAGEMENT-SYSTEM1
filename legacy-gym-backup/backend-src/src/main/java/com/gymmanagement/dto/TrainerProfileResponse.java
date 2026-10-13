package com.gymmanagement.dto;

import com.gymmanagement.entity.TrainerProfile;
import java.math.BigDecimal;

public class TrainerProfileResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private int experienceYears;
    private String bio;
    private BigDecimal hourlyRate;
    private boolean active;

    public TrainerProfileResponse() {
    }

    public static TrainerProfileResponse fromEntity(TrainerProfile profile) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setId(profile.getId());
        if (profile.getUser() != null) {
            response.setUserId(profile.getUser().getId());
            response.setFullName(profile.getUser().getFullName());
            response.setEmail(profile.getUser().getEmail());
            response.setPhone(profile.getUser().getPhone());
        }
        response.setSpecialization(profile.getSpecialization());
        response.setQualification(profile.getQualification());
        response.setExperienceYears(profile.getExperienceYears());
        response.setBio(profile.getBio());
        response.setHourlyRate(profile.getHourlyRate());
        response.setActive(profile.isActive());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
