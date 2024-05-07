package com.example.hrm.service.impl;

import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.repository.VerifyTokenRepository;
import com.example.hrm.service.VerifyTokenService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
@Service
public class VerifyTokenServiceImpl implements VerifyTokenService {
    private final VerifyTokenRepository verifyTokenRepository;

    public VerifyTokenServiceImpl(VerifyTokenRepository verifyTokenRepository) {
        this.verifyTokenRepository = verifyTokenRepository;
    }

    @Override
    public void save(User user, String token) {
        VerifyToken verifyToken = new VerifyToken(token,user);
        verifyToken.setExpireDate(caculateExpiredTime(24*60));
        verifyTokenRepository.save(verifyToken);
    }

    @Override
    public Timestamp caculateExpiredTime(int exprireTimeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, exprireTimeInMinutes);
        return new Timestamp(calendar.getTime().getTime());
    }

    @Override
    public VerifyToken findByUser(User user) {
        return verifyTokenRepository.findByUser(user);
    }

    @Override
    public VerifyToken findByToken(String token) {
        return verifyTokenRepository.findByToken(token);
    }

    @Override
    public void saveToken(VerifyToken verifyToken) {
        verifyTokenRepository.save(verifyToken);
    }
}
