package com.gymmanagement.repository;

import com.gymmanagement.entity.Membership;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByMemberOrderByCreatedAtDesc(User member);
    List<Membership> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    List<Membership> findByStatus(MembershipStatus status);
    List<Membership> findTop10ByOrderByCreatedAtDesc();
    
    // Check if member has active membership on a given date
    @Query("SELECT m FROM Membership m WHERE m.member.id = :memberId AND m.status = 'ACTIVE' AND :currentDate BETWEEN m.startDate AND m.endDate")
    Optional<Membership> findActiveMembershipForMember(@Param("memberId") Long memberId, @Param("currentDate") LocalDate currentDate);

    // Latest active or pending membership
    Optional<Membership> findFirstByMemberIdAndStatusOrderByEndDateDesc(Long memberId, MembershipStatus status);

    long countByStatus(MembershipStatus status);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'ACTIVE' AND :today BETWEEN m.startDate AND m.endDate")
    long countCurrentlyActiveMemberships(@Param("today") LocalDate today);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'EXPIRED' OR (m.status = 'ACTIVE' AND m.endDate < :today)")
    long countExpiredMemberships(@Param("today") LocalDate today);
}
