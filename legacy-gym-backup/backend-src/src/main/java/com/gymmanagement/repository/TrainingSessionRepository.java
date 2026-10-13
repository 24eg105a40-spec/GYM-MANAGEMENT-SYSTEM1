package com.gymmanagement.repository;

import com.gymmanagement.entity.TrainingSession;
import com.gymmanagement.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByMemberIdOrderBySessionDateDescStartTimeDesc(Long memberId);
    List<TrainingSession> findByTrainerIdOrderBySessionDateDescStartTimeDesc(Long trainerId);
    List<TrainingSession> findByTrainerUserIdOrderBySessionDateDescStartTimeDesc(Long trainerUserId);
    List<TrainingSession> findByStatusOrderBySessionDateDescStartTimeDesc(SessionStatus status);

    @Query("SELECT s FROM TrainingSession s WHERE s.member.id = :memberId AND s.sessionDate >= :today AND s.status IN ('PENDING', 'CONFIRMED') ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<TrainingSession> findUpcomingSessionsForMember(@Param("memberId") Long memberId, @Param("today") LocalDate today);

    @Query("SELECT s FROM TrainingSession s WHERE s.trainer.user.id = :trainerUserId AND s.sessionDate >= :today AND s.status IN ('PENDING', 'CONFIRMED') ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<TrainingSession> findUpcomingSessionsForTrainer(@Param("trainerUserId") Long trainerUserId, @Param("today") LocalDate today);

    @Query("SELECT s FROM TrainingSession s WHERE s.sessionDate >= :today AND s.status IN ('PENDING', 'CONFIRMED') ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<TrainingSession> findAllUpcomingSessions(@Param("today") LocalDate today);

    // Overlap checks for trainer
    @Query("SELECT COUNT(s) > 0 FROM TrainingSession s WHERE s.trainer.id = :trainerId AND s.sessionDate = :sessionDate AND s.status = 'CONFIRMED' AND (:startTime < s.endTime AND :endTime > s.startTime)")
    boolean existsOverlappingConfirmedSessionForTrainer(@Param("trainerId") Long trainerId, @Param("sessionDate") LocalDate sessionDate, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    // Overlap checks for member
    @Query("SELECT COUNT(s) > 0 FROM TrainingSession s WHERE s.member.id = :memberId AND s.sessionDate = :sessionDate AND s.status IN ('PENDING', 'CONFIRMED') AND (:startTime < s.endTime AND :endTime > s.startTime)")
    boolean existsOverlappingSessionForMember(@Param("memberId") Long memberId, @Param("sessionDate") LocalDate sessionDate, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    long countByStatus(SessionStatus status);

    @Query("SELECT COUNT(s) FROM TrainingSession s WHERE s.trainer.user.id = :trainerUserId AND s.status = 'PENDING'")
    long countPendingSessionsForTrainer(@Param("trainerUserId") Long trainerUserId);

    @Query("SELECT COUNT(s) FROM TrainingSession s WHERE s.sessionDate >= :today AND s.status IN ('PENDING', 'CONFIRMED')")
    long countUpcomingSessionsTotal(@Param("today") LocalDate today);
}
