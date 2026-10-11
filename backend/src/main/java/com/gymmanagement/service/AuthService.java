package com.gymmanagement.service;

import com.gymmanagement.dto.AuthResponse;
import com.gymmanagement.dto.LoginRequest;
import com.gymmanagement.dto.RegisterRequest;
import com.gymmanagement.dto.UserResponse;
import com.gymmanagement.entity.User;
import com.gymmanagement.enums.Role;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.exception.ResourceNotFoundException;
import com.gymmanagement.repository.UserRepository;
import com.gymmanagement.security.JwtTokenProvider;
import com.gymmanagement.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("Your account has been deactivated. Please contact administrator.");
        }

        return new AuthResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

    @Transactional
    public UserResponse registerMember(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException("Email is already registered: " + registerRequest.getEmail());
        }

        // Public registration strictly creates MEMBER role only
        User user = new User(
                registerRequest.getFullName(),
                registerRequest.getEmail(),
                registerRequest.getPhone(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.MEMBER
        );

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BadRequestException("No authenticated user in context");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));

        return UserResponse.fromEntity(user);
    }
}
