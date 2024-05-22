package com.example.hrm.controller;

import com.example.hrm.dto.request.AddNotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping("/subscription")
    public SseEmitter subsribe() {
        log.info("subscribing...");

        SseEmitter sseEmitter = new SseEmitter(24 * 60 * 60 * 1000L);
        notificationService.addEmitter(sseEmitter);

        log.info("subscribed");
        return sseEmitter;
    }

    @GetMapping("/pushNotification")
    public String pushNotiForm(Model model) {
        AddNotificationRequest addNotificationRequest = new AddNotificationRequest();
        model.addAttribute("addNotificationRequest",addNotificationRequest);
        return "add_notification";
    }

    @PostMapping("/pushNotification")
    public String send(@ModelAttribute AddNotificationRequest request) {
        notificationService.pushNotification(request);
        return "redirect:/";
    }

    @GetMapping("/notification")
    public String showListNotification(Model model) {
        List<NotificationResponse> notificationList = notificationService.getAllNotification();
        model.addAttribute("notificationList",notificationList);
        return "list_notification";
    }

    @GetMapping("notification/{id}")
    public String showNotificationDetail(@PathVariable("id") Long id, Model model) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        if(notificationResponse != null) {
            String formattedDateTime = notificationResponse.getLocalDateTime().format(formatter);
            model.addAttribute("formattedDateTime", formattedDateTime);
        }
        model.addAttribute("notification",notificationResponse);
        return "notification_detail";
    }

    @GetMapping("updateNotification/{id}")
    public String showUpdateNotificationForm(@PathVariable("id") Long id, Model model) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        if(notificationResponse != null) {
            String formattedDateTime = notificationResponse.getLocalDateTime().format(formatter);
            model.addAttribute("formattedDateTime", formattedDateTime);
        }
        model.addAttribute("notification",notificationResponse);
        return "update_notification";
    }

    @PostMapping("/updateNotification/{id}")
    public String updateNotification(@PathVariable("id") Long id, @ModelAttribute AddNotificationRequest request) {
        notificationService.updateNotification(id,request);
        return "redirect:/notification";
    }

    @PostMapping("/deleteNotification/{id}")
    public String deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return "redirect:/notification";
    }
}
