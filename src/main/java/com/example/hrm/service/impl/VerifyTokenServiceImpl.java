package com.example.hrm.service.impl;

import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.repository.VerifyTokenRepository;
import com.example.hrm.service.VerifyTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VerifyTokenServiceImpl implements VerifyTokenService {
    private final VerifyTokenRepository verifyTokenRepository;

    @Override
    public void save(User user, String token) {
        VerifyToken verifyToken = new VerifyToken(token, user);
        verifyToken.setExpireDate(caculateExpiredTime(24 * 60));
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
        return verifyTokenRepository.findByUser(user).orElse(null);
    }

    @Override
    public VerifyToken findByToken(String token) {
        return verifyTokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public void saveToken(VerifyToken verifyToken) {
        verifyTokenRepository.save(verifyToken);
    }

    @Override
    public void deleteToken(VerifyToken verifyToken) {
        verifyTokenRepository.delete(verifyToken);
    }

    @Override
    public void updateToken(User user, String token) {
        Optional<VerifyToken> verifyToken = verifyTokenRepository.findByUser(user);
        if (verifyToken.isPresent()) {
            verifyToken.get().setToken(token);
            verifyToken.get().setExpireDate(caculateExpiredTime(24 * 60));
            saveToken(verifyToken.get());
        }
    }
}
