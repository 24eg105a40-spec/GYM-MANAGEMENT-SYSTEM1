package com.telemetry.controller;

import com.telemetry.model.User;
import com.telemetry.repository.UserRepository;
import com.telemetry.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-samesite:Lax}")
    private String cookieSameSite;

    public AuthController(UserRepository userRepo, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        Optional<User> userOpt = userRepo.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
        }

        if ("REJECTED".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account registration was declined"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        attachCookie(response, token);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole(),
                "status", user.getStatus()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String fullName = body.get("fullName");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        if (userRepo.existsByEmail(email.trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already in use"));
        }

        User newUser = new User(
                email.trim(),
                passwordEncoder.encode(password),
                fullName.trim(),
                "CLIENT",
                "ACTIVE"
        );

        User saved = userRepo.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "fullName", saved.getFullName(),
                "role", saved.getRole(),
                "status", saved.getStatus()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie c = new Cookie(cookieName, "");
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(0);
        c.setSecure(cookieSecure);
        response.addCookie(c);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (auth.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole(),
                    "status", user.getStatus()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private void attachCookie(HttpServletResponse res, String token) {
        String header = String.format(
                "%s=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=%s%s",
                cookieName, token, jwtService.getExpirySeconds(),
                cookieSameSite, cookieSecure ? "; Secure" : ""
        );
        res.addHeader("Set-Cookie", header);
    }
}
