package com.example.hrm.repository;

import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyTokenRepository extends JpaRepository<VerifyToken,Long> {
    VerifyToken findByUser(User user);
    VerifyToken findByToken(String token);
}
