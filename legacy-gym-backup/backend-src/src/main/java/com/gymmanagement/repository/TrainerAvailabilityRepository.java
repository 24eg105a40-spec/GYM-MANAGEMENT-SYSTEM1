package com.gymmanagement.repository;

import com.gymmanagement.entity.TrainerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainerAvailabilityRepository extends JpaRepository<TrainerAvailability, Long> {
    List<TrainerAvailability> findByTrainerIdAndAvailabilityDateGreaterThanEqualOrderByAvailabilityDateAscStartTimeAsc(Long trainerId, LocalDate date);
    List<TrainerAvailability> findByTrainerUserIdAndAvailabilityDateGreaterThanEqualOrderByAvailabilityDateAscStartTimeAsc(Long trainerUserId, LocalDate date);
    List<TrainerAvailability> findByTrainerIdAndAvailableTrueAndAvailabilityDateGreaterThanEqualOrderByAvailabilityDateAscStartTimeAsc(Long trainerId, LocalDate date);
    List<TrainerAvailability> findByTrainerUserIdOrderByAvailabilityDateDesc(Long trainerUserId);
}
