package com.telemetry.repository;

import com.telemetry.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(String role);
    List<User> findAllByStatus(String status);
    boolean existsByEmail(String email);
}
