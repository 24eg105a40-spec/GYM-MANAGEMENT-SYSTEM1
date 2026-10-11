package com.gymmanagement.service;

import com.gymmanagement.dto.MembershipResponseDto;
import com.gymmanagement.dto.UserProfileUpdateRequest;
import com.gymmanagement.dto.UserResponse;
import com.gymmanagement.entity.Membership;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.Role;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.MembershipRepository;
import com.gymmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public MemberService(UserRepository userRepository, MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getMemberProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + userId));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateMemberProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + userId));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllMembers() {
        return getAllMembers(null);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllMembers(Role role) {
        List<User> users;
        if (role != null) {
            users = userRepository.findByRoleOrderByCreatedAtDesc(role);
        } else {
            users = userRepository.findAllByOrderByCreatedAtDesc();
        }
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getMemberById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateMemberStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        user.setActive(active);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (Exception ex) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDto> getMemberMembershipHistory(Long memberId) {
        if (!userRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        List<Membership> memberships = membershipRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return memberships.stream()
                .map(MembershipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
