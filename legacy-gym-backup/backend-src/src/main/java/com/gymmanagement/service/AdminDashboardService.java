package com.gymmanagement.service;

import com.gymmanagement.dto.*;
import com.gymmanagement.entity.*;
import com.gymmanagement.enums.MembershipStatus;
import com.gymmanagement.enums.PaymentStatus;
import com.gymmanagement.enums.Role;
import com.gymmanagement.enums.SessionStatus;
import com.gymmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final TrainingSessionRepository sessionRepository;
    private final TrainerAssignmentRepository assignmentRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final FitnessProgressRepository progressRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            MembershipPlanRepository planRepository,
            TrainerProfileRepository trainerProfileRepository,
            PaymentRepository paymentRepository,
            AttendanceRepository attendanceRepository,
            TrainingSessionRepository sessionRepository,
            TrainerAssignmentRepository assignmentRepository,
            WorkoutPlanRepository workoutPlanRepository,
            FitnessProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.planRepository = planRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
        this.sessionRepository = sessionRepository;
        this.assignmentRepository = assignmentRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getAdminDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime firstDayOfMonth = today.withDayOfMonth(1).atStartOfDay();

        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();
        stats.setTotalMembers(userRepository.countByRole(Role.MEMBER));
        stats.setActiveMembers(membershipRepository.countCurrentlyActiveMemberships(today));
        stats.setExpiredMemberships(membershipRepository.countExpiredMemberships(today));
        stats.setPendingMemberships(membershipRepository.countByStatus(MembershipStatus.PENDING));
        stats.setTotalActiveTrainers(trainerProfileRepository.countByActive(true));
        stats.setTotalPlans(planRepository.countByActive(true));
        stats.setTodayAttendance(attendanceRepository.countByAttendanceDate(today));
        stats.setPendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING));
        stats.setUpcomingSessions(sessionRepository.countUpcomingSessionsTotal(today));

        BigDecimal totalRevenue = paymentRepository.sumTotalVerifiedRevenue();
        stats.setTotalVerifiedRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        BigDecimal monthlyRevenue = paymentRepository.sumVerifiedRevenueSince(firstDayOfMonth);
        stats.setMonthlyVerifiedRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);

        List<PaymentResponseDto> recentPayments = paymentRepository.findTop10ByOrderByPaymentDateDesc().stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
        stats.setRecentPayments(recentPayments);

        List<MembershipResponseDto> recentMemberships = membershipRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(MembershipResponseDto::fromEntity)
                .collect(Collectors.toList());
        stats.setRecentMemberships(recentMemberships);

        List<UserResponse> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        stats.setRecentUsers(recentUsers);

        return stats;
    }

    @Transactional(readOnly = true)
    public MemberDashboardStatsDto getMemberDashboardStats(Long memberId) {
        LocalDate today = LocalDate.now();
        MemberDashboardStatsDto dto = new MemberDashboardStatsDto();

        // Active membership
        membershipRepository.findActiveMembershipForMember(memberId, today)
                .ifPresent(m -> dto.setActiveMembership(MembershipResponseDto.fromEntity(m)));

        // If no active, check if there is a pending membership
        if (dto.getActiveMembership() == null) {
            membershipRepository.findFirstByMemberIdAndStatusOrderByEndDateDesc(memberId, MembershipStatus.PENDING)
                    .ifPresent(m -> dto.setActiveMembership(MembershipResponseDto.fromEntity(m)));
        }

        // Attendance summary
        boolean checkedInToday = attendanceRepository.existsByMemberIdAndAttendanceDate(memberId, today);
        long totalAttendance = attendanceRepository.countTotalAttendanceByMember(memberId);
        long last30Days = attendanceRepository.countAttendanceByMemberSince(memberId, today.minusDays(30));
        long monthAttendance = attendanceRepository.countAttendanceByMemberSince(memberId, today.withDayOfMonth(1));
        dto.setAttendanceSummary(new AttendanceSummaryDto(checkedInToday, totalAttendance, last30Days, monthAttendance));

        // Assigned Trainer
        assignmentRepository.findByMemberIdAndActive(memberId, true)
                .ifPresent(a -> dto.setAssignedTrainer(TrainerProfileResponse.fromEntity(a.getTrainer())));

        // Current Workout Plan
        workoutPlanRepository.findFirstByMemberIdAndActiveOrderByStartDateDesc(memberId, true)
                .ifPresent(wp -> dto.setCurrentWorkoutPlan(WorkoutPlanResponse.fromEntity(wp)));

        // Latest Progress
        progressRepository.findFirstByMemberIdOrderByMeasurementDateDesc(memberId)
                .ifPresent(p -> dto.setLatestProgress(FitnessProgressResponse.fromEntity(p)));

        // Upcoming Sessions
        List<TrainingSessionResponse> upcoming = sessionRepository.findUpcomingSessionsForMember(memberId, today).stream()
                .map(TrainingSessionResponse::fromEntity)
                .collect(Collectors.toList());
        dto.setUpcomingSessions(upcoming);

        // Recent Attendance
        List<AttendanceResponseDto> recentAttendance = attendanceRepository.findByMemberIdOrderByAttendanceDateDesc(memberId).stream()
                .limit(7)
                .map(AttendanceResponseDto::fromEntity)
                .collect(Collectors.toList());
        dto.setRecentAttendance(recentAttendance);

        return dto;
    }

    @Transactional(readOnly = true)
    public TrainerDashboardStatsDto getTrainerDashboardStats(Long trainerUserId) {
        LocalDate today = LocalDate.now();
        TrainerDashboardStatsDto dto = new TrainerDashboardStatsDto();

        dto.setAssignedMembersCount(assignmentRepository.countByTrainerUserIdAndActive(trainerUserId, true));
        dto.setPendingSessionsCount(sessionRepository.countPendingSessionsForTrainer(trainerUserId));

        List<TrainingSession> upcoming = sessionRepository.findUpcomingSessionsForTrainer(trainerUserId, today);
        dto.setUpcomingSessionsCount(upcoming.size());
        dto.setUpcomingSessions(upcoming.stream().map(TrainingSessionResponse::fromEntity).collect(Collectors.toList()));

        List<TrainingSessionResponse> todaySessions = upcoming.stream()
                .filter(s -> s.getSessionDate().isEqual(today))
                .map(TrainingSessionResponse::fromEntity)
                .collect(Collectors.toList());
        dto.setTodaySessions(todaySessions);

        List<MemberSummaryDto> assignedMembers = assignmentRepository.findByTrainerUserIdAndActive(trainerUserId, true).stream()
                .map(a -> MemberSummaryDto.fromEntity(a.getMember()))
                .collect(Collectors.toList());
        dto.setAssignedMembers(assignedMembers);

        return dto;
    }
}
