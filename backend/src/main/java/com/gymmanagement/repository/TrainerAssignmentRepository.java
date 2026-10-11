package com.gymmanagement.repository;

import com.gymmanagement.entity.TrainerAssignment;
import com.gymmanagement.entity.TrainerProfile;
import com.gymmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerAssignmentRepository extends JpaRepository<TrainerAssignment, Long> {
    Optional<TrainerAssignment> findByMemberAndActive(User member, boolean active);
    Optional<TrainerAssignment> findByMemberIdAndActive(Long memberId, boolean active);
    List<TrainerAssignment> findByTrainerAndActive(TrainerProfile trainer, boolean active);
    List<TrainerAssignment> findByTrainerIdAndActive(Long trainerId, boolean active);
    List<TrainerAssignment> findByTrainerUserIdAndActive(Long trainerUserId, boolean active);
    List<TrainerAssignment> findByMemberIdOrderByAssignedAtDesc(Long memberId);
    long countByTrainerIdAndActive(Long trainerId, boolean active);
    long countByTrainerUserIdAndActive(Long trainerUserId, boolean active);
    boolean existsByMemberIdAndTrainerIdAndActive(Long memberId, Long trainerId, boolean active);
}
