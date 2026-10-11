package com.gymmanagement.service;

import com.gymmanagement.dto.MembershipRequestDto;
import com.gymmanagement.dto.MembershipResponseDto;
import com.gymmanagement.entity.Membership;
import com.gymmanagement.entity.MembershipPlan;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.MembershipStatus;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ForbiddenException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.MembershipPlanRepository;
import com.gymmanagement.repository.MembershipRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final UserRepository userRepository;

    public MembershipService(
            MembershipRepository membershipRepository,
            MembershipPlanRepository planRepository,
            UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MembershipResponseDto createMembershipRequest(Long memberId, MembershipRequestDto request) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + request.getPlanId()));

        if (!plan.isActive()) {
            throw new BadRequestException("Selected membership plan is currently inactive");
        }

        // Determine start date: If member has an active membership, start after the active one ends; otherwise today (or preferred date)
        LocalDate startDate = request.getStartDate() != null && !request.getStartDate().isBefore(LocalDate.now())
                ? request.getStartDate()
                : LocalDate.now();

        Optional<Membership> activeMembership = membershipRepository.findActiveMembershipForMember(memberId, LocalDate.now());
        if (activeMembership.isPresent()) {
            LocalDate currentEnd = activeMembership.get().getEndDate();
            if (currentEnd.isAfter(startDate)) {
                startDate = currentEnd.plusDays(1);
            }
        }

        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        Membership membership = new Membership(
                member,
                plan,
                startDate,
                endDate,
                MembershipStatus.PENDING
        );

        Membership saved = membershipRepository.save(membership);
        return MembershipResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDto> getMyMemberships(Long memberId) {
        List<Membership> list = membershipRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return list.stream()
                .map(MembershipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipResponseDto getMembershipById(Long id, Long currentUserId, boolean isAdmin) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));

        if (!isAdmin && !membership.getMember().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not authorized to view this membership record");
        }

        return MembershipResponseDto.fromEntity(membership);
    }

    @Transactional
    public MembershipResponseDto renewMembership(Long membershipId, Long memberId) {
        Membership existing = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));

        if (!existing.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("You can only renew your own membership");
        }

        MembershipPlan plan = existing.getMembershipPlan();
        if (!plan.isActive()) {
            throw new BadRequestException("The plan associated with this membership is no longer active");
        }

        // If currently active, start from existing end date + 1 day; else start from today
        LocalDate startDate;
        if (existing.getStatus() == MembershipStatus.ACTIVE && existing.getEndDate().isAfter(LocalDate.now())) {
            startDate = existing.getEndDate().plusDays(1);
        } else {
            startDate = LocalDate.now();
        }

        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        Membership renewal = new Membership(
                existing.getMember(),
                plan,
                startDate,
                endDate,
                MembershipStatus.PENDING
        );

        Membership saved = membershipRepository.save(renewal);
        return MembershipResponseDto.fromEntity(saved);
    }

    @Transactional
    public MembershipResponseDto cancelMembership(Long membershipId, Long memberId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));

        if (!membership.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("You can only cancel your own membership");
        }

        if (membership.getStatus() != MembershipStatus.PENDING) {
            throw new BadRequestException("Only pending memberships can be cancelled by members. Please contact admin for active plans.");
        }

        membership.setStatus(MembershipStatus.CANCELLED);
        return MembershipResponseDto.fromEntity(membershipRepository.save(membership));
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDto> getAllMemberships() {
        return membershipRepository.findAll().stream()
                .map(MembershipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MembershipResponseDto activateMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));

        // Adjust dates to start from today if pending start date was in the past
        if (membership.getStartDate().isBefore(LocalDate.now())) {
            int duration = membership.getMembershipPlan().getDurationDays();
            membership.setStartDate(LocalDate.now());
            membership.setEndDate(LocalDate.now().plusDays(duration));
        }

        membership.setStatus(MembershipStatus.ACTIVE);
        Membership saved = membershipRepository.save(membership);
        return MembershipResponseDto.fromEntity(saved);
    }

    @Transactional
    public MembershipResponseDto updateMembershipStatus(Long membershipId, MembershipStatus status) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));

        membership.setStatus(status);
        Membership saved = membershipRepository.save(membership);
        return MembershipResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Optional<MembershipResponseDto> getActiveMembershipForMember(Long memberId) {
        return membershipRepository.findActiveMembershipForMember(memberId, LocalDate.now())
                .map(MembershipResponseDto::fromEntity);
    }
}
