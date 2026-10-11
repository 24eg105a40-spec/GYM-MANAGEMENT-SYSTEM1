package com.gymmanagement.repository;

import com.gymmanagement.entity.Attendance;
import com.gymmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByMemberAndAttendanceDate(User member, LocalDate attendanceDate);
    Optional<Attendance> findByMemberIdAndAttendanceDate(Long memberId, LocalDate attendanceDate);
    boolean existsByMemberIdAndAttendanceDate(Long memberId, LocalDate attendanceDate);

    List<Attendance> findByMemberIdOrderByAttendanceDateDesc(Long memberId);
    List<Attendance> findByMemberIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long memberId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByAttendanceDateOrderByCheckInTimeDesc(LocalDate date);
    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDesc(LocalDate startDate, LocalDate endDate);

    long countByAttendanceDate(LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.id = :memberId")
    long countTotalAttendanceByMember(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.id = :memberId AND a.attendanceDate >= :startDate")
    long countAttendanceByMemberSince(@Param("memberId") Long memberId, @Param("startDate") LocalDate startDate);
}
