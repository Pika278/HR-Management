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
    private static final int PAGE_SIZE = 10;
    private static final String SORT_BY_ID = "id";

    @GetMapping("/subscription")
    public SseEmitter subsribe() {
        log.info("subscribing...");
        SseEmitter sseEmitter = new SseEmitter(24 * 60 * 60 * 1000L);
        notificationService.addEmitter(sseEmitter);
        log.info("subscribed");
        return sseEmitter;
    }

    @GetMapping("/notification/page/{pageNum}")
    public String showListNotification(@PathVariable(name = "pageNum") int pageNum, @RequestParam(required = false) String keyword, Model model) {
        Page<NotificationResponse> notificationList = notificationService.getAllNotificationByDescPaging(pageNum,PAGE_SIZE,SORT_BY_ID,keyword);
        model.addAttribute("totalPages", notificationList.getTotalPages());
        model.addAttribute("totalItems", notificationList.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("sortBy", SORT_BY_ID);
        model.addAttribute("keyword", keyword);
        model.addAttribute("notificationList", notificationList);
        return "list_notification";
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
