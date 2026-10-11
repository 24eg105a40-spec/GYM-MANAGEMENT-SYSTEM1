package com.gymmanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:9a4f2c8d3e71b5a6c4d8e2f1a3b5c7d9e0f2a4b6c8d1e3f5a7b9c1d3e5f7a9b1}") String jwtSecret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("role", userPrincipal.getRole().name())
                .claim("fullName", userPrincipal.getFullName())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("id", Long.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
