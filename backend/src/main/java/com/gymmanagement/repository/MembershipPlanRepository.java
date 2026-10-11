package com.gymmanagement.repository;

import com.gymmanagement.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByName(String name);
    boolean existsByName(String name);
    List<MembershipPlan> findByActive(boolean active);
    long countByActive(boolean active);
}
