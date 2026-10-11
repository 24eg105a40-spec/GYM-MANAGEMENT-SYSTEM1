package com.gymmanagement.service;

import com.gymmanagement.dto.AttendanceCorrectionRequest;
import com.gymmanagement.dto.AttendanceResponseDto;
import com.gymmanagement.dto.AttendanceSummaryDto;
import com.gymmanagement.entity.Attendance;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.AttendanceStatus;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.AttendanceRepository;
import com.gymmanagement.repository.MembershipRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            MembershipRepository membershipRepository,
            UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AttendanceResponseDto checkIn(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (!member.isActive()) {
            throw new BadRequestException("Account is deactivated. Check-in not allowed.");
        }

        LocalDate today = LocalDate.now();

        // Check if member already checked in today
        if (attendanceRepository.existsByMemberIdAndAttendanceDate(memberId, today)) {
            throw new ConflictException("You have already checked in for today: " + today);
        }

        // Validate that member has an active membership valid today
        boolean hasActiveMembership = membershipRepository.findActiveMembershipForMember(memberId, today).isPresent();
        if (!hasActiveMembership) {
            throw new BadRequestException("Cannot check in: You do not have an active membership valid for today. Please purchase or renew a plan.");
        }

        Attendance attendance = new Attendance(
                member,
                today,
                LocalTime.now(),
                AttendanceStatus.PRESENT
        );

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getMyAttendance(Long memberId) {
        return attendanceRepository.findByMemberIdOrderByAttendanceDateDesc(memberId).stream()
                .map(AttendanceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryDto getMyAttendanceSummary(Long memberId) {
        LocalDate today = LocalDate.now();
        boolean checkedInToday = attendanceRepository.existsByMemberIdAndAttendanceDate(memberId, today);
        long total = attendanceRepository.countTotalAttendanceByMember(memberId);
        long last30Days = attendanceRepository.countAttendanceByMemberSince(memberId, today.minusDays(30));
        long currentMonth = attendanceRepository.countAttendanceByMemberSince(memberId, today.withDayOfMonth(1));

        return new AttendanceSummaryDto(checkedInToday, total, last30Days, currentMonth);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAllAttendance(LocalDate date) {
        List<Attendance> list = (date != null)
                ? attendanceRepository.findByAttendanceDateOrderByCheckInTimeDesc(date)
                : attendanceRepository.findAll();

        return list.stream()
                .map(AttendanceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getTodayAttendanceCount() {
        return attendanceRepository.countByAttendanceDate(LocalDate.now());
    }

    @Transactional
    public AttendanceResponseDto correctAttendance(Long id, AttendanceCorrectionRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        attendance.setStatus(request.getStatus());
        if (request.getCheckInTime() != null) {
            attendance.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            attendance.setCheckOutTime(request.getCheckOutTime());
        }

        Attendance updated = attendanceRepository.save(attendance);
        return AttendanceResponseDto.fromEntity(updated);
    }
}
