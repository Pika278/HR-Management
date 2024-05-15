package com.example.hrm.service;

import com.example.hrm.entity.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendHTMLMail(User user) throws MessagingException;
    void sendForgotPasswordMail(User user) throws MessagingException;
}
