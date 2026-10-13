package com.gymmanagement.repository;

import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {
    Optional<TrainerProfile> findByUser(User user);
    Optional<TrainerProfile> findByUserId(Long userId);
    List<TrainerProfile> findByActive(boolean active);
    long countByActive(boolean active);
}
