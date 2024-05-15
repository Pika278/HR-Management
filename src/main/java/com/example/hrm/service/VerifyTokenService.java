package com.example.hrm.service;

import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;

import java.sql.Timestamp;
import java.util.Optional;

public interface VerifyTokenService {
    void save(User user, String token);
    Timestamp caculateExpiredTime(int exprireTimeInMinutes);
    VerifyToken findByUser(User user);
    VerifyToken  findByToken(String token);
    void saveToken(VerifyToken verifyToken);
    void deleteToken(VerifyToken verifyToken);
    void updateToken(User user,String token);
}
