package com.example.hrm.controller.user;

import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.exception.AppException;
import com.example.hrm.mapper.NotificationMapper;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserNotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;


    @GetMapping("/subscription")
    public SseEmitter subsribe() {
        log.info("subscribing...");
        SseEmitter sseEmitter = new SseEmitter(24 * 60 * 60 * 1000L);
        notificationService.addEmitter(sseEmitter);
        log.info("subscribed");
        return sseEmitter;
    }

    @GetMapping("notification/{id}")
    public String showNotificationDetail(@PathVariable("id") Long id, Model model) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        model.addAttribute("isPublished", notificationResponse.isPublished());
        model.addAttribute("notification", notificationResponse);
        return "notification_detail";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
