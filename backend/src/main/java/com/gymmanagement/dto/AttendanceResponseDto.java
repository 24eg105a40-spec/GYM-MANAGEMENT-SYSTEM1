package com.gymmanagement.dto;

import com.gymmanagement.entity.Attendance;
import com.gymmanagement.enums.AttendanceStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceResponseDto {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private AttendanceStatus status;
    private LocalDateTime createdAt;

    public AttendanceResponseDto() {
    }

    public static AttendanceResponseDto fromEntity(Attendance attendance) {
        AttendanceResponseDto dto = new AttendanceResponseDto();
        dto.setId(attendance.getId());
        if (attendance.getMember() != null) {
            dto.setMemberId(attendance.getMember().getId());
            dto.setMemberName(attendance.getMember().getFullName());
            dto.setMemberEmail(attendance.getMember().getEmail());
        }
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setStatus(attendance.getStatus());
        dto.setCreatedAt(attendance.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
