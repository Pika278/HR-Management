package com.example.hrm.controller.user;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.entity.User;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserNotificationController {
    private final NotificationService notificationService;
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
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userLoggedIn = myUserDetails.getUser();
        Page<NotificationResponse> notificationPage;
        if (userLoggedIn.getRole() == Role.ADMIN) {
            notificationPage = notificationService.getAllNotificationByDescPaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);

        } else {
            notificationPage = notificationService.findAllPublishedOrderByDescPaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);
        }
        List<NotificationResponse> notificationList = notificationPage.getContent();
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("totalItems", notificationPage.getTotalElements());
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
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userLoggedIn = myUserDetails.getUser();
        if (!notificationResponse.isPublished() && userLoggedIn.getRole() != Role.ADMIN) {
//            return "redirect:/notification/page/1?keyword=";
            return "error/error_403";

        }
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
