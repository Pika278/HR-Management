package com.example.hrm.repository;

import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyTokenRepository extends JpaRepository<VerifyToken, Long> {
    Optional<VerifyToken> findByUser(User user);

    Optional<VerifyToken> findByToken(String token);
}
