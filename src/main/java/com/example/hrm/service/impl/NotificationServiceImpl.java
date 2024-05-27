package com.example.hrm.service.impl;

import com.example.hrm.dto.request.NotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.entity.Notification;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.mapper.NotificationMapper;
import com.example.hrm.repository.NotificationRepository;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ThreadPoolTaskScheduler taskScheduler;
    List<SseEmitter> emitters = new ArrayList<>();

    @Override
    public List<NotificationResponse> getTenPublishedNotification() {
        List<Notification> notificationList = notificationRepository.findTenPublishedOrderByDesc();
        List<NotificationResponse> responseList = new ArrayList<>();
        for (Notification notification : notificationList) {
            responseList.add(notificationMapper.toNotificationResponse(notification));
        }
        return responseList;
    }

    @Override
    public Page<NotificationResponse> findAllPublishedOrderByDescPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<Notification> findAllPublishedOrderByDescPaging = notificationRepository.findAllPublishedOrderByDescPaging(pageable, keyword);
        return findAllPublishedOrderByDescPaging.map(notificationMapper :: toNotificationResponse);
    }

    public void addEmitter(SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitters.add(emitter);
        log.info("add emitter: {}", emitter);
    }

    public void pushNotification(NotificationRequest addNotificationRequest) {
        log.info("pushing notification {}", addNotificationRequest);
        List<SseEmitter> deadEmitters = new ArrayList<>();

        Notification notification = Notification
                .builder()
                .title(addNotificationRequest.getTitle())
                .message(addNotificationRequest.getMessage())
                .publishedTime(addNotificationRequest.getPublishedTime())
                .build();
        notificationRepository.save(notification);
        log.info("emitter.size() when push noti{}", emitters.toString());
        Runnable task = () -> {
            for(Iterator<SseEmitter> emitter = emitters.iterator();emitter.hasNext();) {
                SseEmitter emitterNext = emitter.next();
                try {
                    emitterNext.send(SseEmitter
                            .event()
                            .data(notification));

                } catch (IOException e) {
                    deadEmitters.add(emitterNext);
                }
            }
            emitters.removeAll(deadEmitters);
        };
        ZonedDateTime zonedDateTime = ZonedDateTime.of(notification.getPublishedTime(), ZoneId.systemDefault());
        taskScheduler.schedule(task, zonedDateTime.toInstant());
    }

    @Override
    public NotificationResponse findNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            return notificationMapper.toNotificationResponse(notification);
        } else {
            throw new AppException(ErrorMessage.NOTIFICATION_NOT_FOUND);
        }
    }

    @Override
    public void updateNotification(Long id, NotificationRequest addNotificationRequest) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setPublishedTime(addNotificationRequest.getPublishedTime());
            notification.setTitle(addNotificationRequest.getTitle());
            notification.setMessage(addNotificationRequest.getMessage());
            notificationRepository.save(notification);
        } else {
            throw new AppException(ErrorMessage.NOTIFICATION_NOT_FOUND);
        }

    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notificationRepository.delete(notification);
        } else {
            throw new AppException(ErrorMessage.NOTIFICATION_NOT_FOUND);
        }
    }

    @Override
    public Page<NotificationResponse> getAllNotificationByDescPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<Notification> getAllNotificationByDescPaging = notificationRepository.findAllOrderByDescPaging(pageable, keyword);
        return getAllNotificationByDescPaging.map(notificationMapper :: toNotificationResponse);
    }
}
