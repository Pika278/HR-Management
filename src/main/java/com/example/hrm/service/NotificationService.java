package com.example.hrm.service;

import com.example.hrm.dto.request.NotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getTenPublishedNotification();
    Page<NotificationResponse> findAllPublishedOrderByDescPaging(int pageNumber, int pageSize, String sortBy, String keyword);

    void addEmitter(SseEmitter emitter);

    void pushNotification(NotificationRequest addNotificationRequest);

    NotificationResponse findNotificationById(Long id);

    void updateNotification(Long id, NotificationRequest addNotificationRequest);

    void deleteNotification(Long id);

    Page<NotificationResponse> getAllNotificationByDescPaging(int pageNumber, int pageSize, String sortBy, String keyword);
}
