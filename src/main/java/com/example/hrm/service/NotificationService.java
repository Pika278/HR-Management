package com.example.hrm.service;

import com.example.hrm.dto.request.AddNotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotification();
    void addEmitter(SseEmitter emitter);
    void pushNotification(AddNotificationRequest addNotificationRequest);
    NotificationResponse findNotificationById(Long id);
    void updateNotification(Long id, AddNotificationRequest addNotificationRequest);
    void deleteNotification(Long id);
}
