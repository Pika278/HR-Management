package com.example.hrm.service.impl;

import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.service.EmailService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
@Service
public class EmailServiceImpl implements EmailService {
    private final VerifyTokenService verifyTokenService;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(VerifyTokenService verifyTokenService, TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.verifyTokenService = verifyTokenService;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendHTMLMail(User user) throws MessagingException {
        VerifyToken verifyToken = verifyTokenService.findByUser(user);
        if(verifyToken != null) {
            String token = verifyToken.getToken();
            Context context = new Context();
            context.setVariable("title","Verify your account");
            context.setVariable("link", "http://localhost:8080/activation?token=" + token);
            String body = templateEngine.process("verification",context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Xác thực tài khoản");
            mimeMessageHelper.setText(body,true);
            javaMailSender.send(mimeMessage);
        }
    }
}
