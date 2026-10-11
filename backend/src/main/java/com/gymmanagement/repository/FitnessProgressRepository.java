package com.gymmanagement.repository;

import com.gymmanagement.entity.FitnessProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessProgressRepository extends JpaRepository<FitnessProgress, Long> {
    List<FitnessProgress> findByMemberIdOrderByMeasurementDateAsc(Long memberId);
    Optional<FitnessProgress> findFirstByMemberIdOrderByMeasurementDateDesc(Long memberId);
}
