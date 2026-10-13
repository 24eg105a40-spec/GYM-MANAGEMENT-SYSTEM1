package com.gymmanagement.service;

import com.gymmanagement.dto.MembershipPlanRequest;
import com.gymmanagement.dto.MembershipPlanResponse;
import com.gymmanagement.entity.MembershipPlan;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    public MembershipPlanService(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanResponse> getAllPlans(boolean activeOnly) {
        List<MembershipPlan> plans = activeOnly
                ? planRepository.findByActive(true)
                : planRepository.findAll();

        return plans.stream()
                .map(MembershipPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipPlanResponse getPlanById(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        return MembershipPlanResponse.fromEntity(plan);
    }

    @Transactional
    public MembershipPlanResponse createPlan(MembershipPlanRequest request) {
        if (planRepository.existsByName(request.getName())) {
            throw new ConflictException("Membership plan name already exists: " + request.getName());
        }

        MembershipPlan plan = new MembershipPlan(
                request.getName(),
                request.getDescription(),
                request.getDurationDays(),
                request.getPrice(),
                request.getFacilities(),
                request.isIncludesPersonalTraining()
        );
        plan.setActive(request.isActive());

        MembershipPlan saved = planRepository.save(plan);
        return MembershipPlanResponse.fromEntity(saved);
    }

    @Transactional
    public MembershipPlanResponse updatePlan(Long id, MembershipPlanRequest request) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));

        // Check if updating name collides with another plan
        if (!plan.getName().equalsIgnoreCase(request.getName()) && planRepository.existsByName(request.getName())) {
            throw new ConflictException("Another plan with name '" + request.getName() + "' already exists");
        }

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setDurationDays(request.getDurationDays());
        plan.setPrice(request.getPrice());
        plan.setFacilities(request.getFacilities());
        plan.setIncludesPersonalTraining(request.isIncludesPersonalTraining());
        plan.setActive(request.isActive());

        MembershipPlan updated = planRepository.save(plan);
        return MembershipPlanResponse.fromEntity(updated);
    }

    @Transactional
    public MembershipPlanResponse updatePlanStatus(Long id, boolean active) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        plan.setActive(active);
        return MembershipPlanResponse.fromEntity(planRepository.save(plan));
    }
}
