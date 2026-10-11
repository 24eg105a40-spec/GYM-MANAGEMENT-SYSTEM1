package com.gymmanagement.controller;

import com.gymmanagement.dto.AuthResponse;
import com.gymmanagement.dto.LoginRequest;
import com.gymmanagement.dto.RegisterRequest;
import com.gymmanagement.dto.UserResponse;
import com.gymmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.registerMember(registerRequest), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
