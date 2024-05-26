package com.example.hrm.service;

import com.example.hrm.dto.request.NotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotification();

    void addEmitter(SseEmitter emitter);

    void pushNotification(NotificationRequest addNotificationRequest);

    NotificationResponse findNotificationById(Long id);

    void updateNotification(Long id, NotificationRequest addNotificationRequest);

    void deleteNotification(Long id);
}
