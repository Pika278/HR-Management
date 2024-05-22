package com.example.hrm.service.impl;

import com.example.hrm.dto.request.AddNotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.entity.Notification;
import com.example.hrm.mapper.NotificationMapper;
import com.example.hrm.repository.NotificationRepository;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    List<SseEmitter> emitters = new ArrayList<>();

    @Override
    public List<NotificationResponse> getAllNotification() {
        List<Notification> notificationList = notificationRepository.findAllOrderByDesc();
        List<NotificationResponse> responseList = new ArrayList<>();
        for(Notification notification : notificationList) {
            responseList.add(notificationMapper.toNotificationResponse(notification));
        }
        return responseList;
    }

    public void addEmitter(SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitters.add(emitter);
    }

    public void pushNotification(AddNotificationRequest addNotificationRequest) {
        log.info("pushing notification {}", addNotificationRequest);
        List<SseEmitter> deadEmitters = new ArrayList<>();

        Notification notification = Notification
                .builder()
                .title(addNotificationRequest.getTitle())
                .message(addNotificationRequest.getMessage())
                .build();
        notificationRepository.save(notification);

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter
                        .event()
                        .data(notification));

            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }

    @Override
    public NotificationResponse findNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public void updateNotification(Long id, AddNotificationRequest addNotificationRequest) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if(notification != null) {
            notification.setTitle(addNotificationRequest.getTitle());
            notification.setMessage(addNotificationRequest.getMessage());
            notificationRepository.save(notification);
        }
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if(notification != null) {
            notificationRepository.delete(notification);
        }
    }
}
