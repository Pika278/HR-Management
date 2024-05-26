package com.example.hrm.controller;

import com.example.hrm.dto.request.NotificationRequest;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.exception.AppException;
import com.example.hrm.mapper.NotificationMapper;
import com.example.hrm.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/pushNotification")
    public String pushNotiForm(Model model) {
        if(!model.containsAttribute("addNotificationRequest")) {
            NotificationRequest addNotificationRequest = new NotificationRequest();
            model.addAttribute("addNotificationRequest", addNotificationRequest);
        }
        return "add_notification";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pushNotification")
    public String send(@Valid @ModelAttribute("addNotificationRequest") NotificationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (request.getPublishedTime() != null && request.getPublishedTime().isBefore(LocalDateTime.now())) {
            bindingResult.addError(new FieldError("request", "publishedTime", "Thời gian gửi phải lớn hơn bây giờ"));
        }
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasErrors()) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    redirectAttributes.addFlashAttribute(error.getField(), error.getDefaultMessage());
                }
                redirectAttributes.addFlashAttribute("addNotificationRequest", request);
                return "redirect:/pushNotification";
            }
        }
        notificationService.pushNotification(request);
        return "redirect:/";
    }

    @GetMapping("/notification")
    public String showListNotification(Model model) {
        List<NotificationResponse> notificationList = notificationService.getAllNotification();
        model.addAttribute("notificationList", notificationList);
        return "list_notification";
    }

    @GetMapping("notification/{id}")
    public String showNotificationDetail(@PathVariable("id") Long id, Model model) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
//        notificationResponse.setPublished(notificationResponse.getPublishedTime().isBefore(LocalDateTime.now()));
        model.addAttribute("isPublished", notificationResponse.isPublished());
        model.addAttribute("notification", notificationResponse);
        return "notification_detail";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("updateNotification/{id}")
    public String showUpdateNotificationForm(@PathVariable("id") Long id, Model model) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        if (notificationResponse.isPublished()) {
            return "redirect:/notification/" + id;
        }
        if(!model.containsAttribute("notification")) {
            model.addAttribute("notificationId", id);
            model.addAttribute("notification", notificationResponse);
        }
        return "update_notification";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateNotification/{id}")
    public String updateNotification(@PathVariable("id") Long id, @Valid @ModelAttribute("notification") NotificationRequest request,
                                     BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        if (notificationResponse.isPublished()) {
            return "redirect:/notification/" + id;
        }
        if (request.getPublishedTime() != null && request.getPublishedTime().isBefore(LocalDateTime.now())) {
            bindingResult.addError(new FieldError("request", "publishedTime", "Thời gian gửi phải lớn hơn bây giờ"));
        }
        if (bindingResult.hasErrors()) {
            for(FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute(error.getField(),error.getDefaultMessage());
            }
            NotificationResponse updateNotificationResponse = notificationMapper.notificationRequesttoNotificationResponse(request);
            redirectAttributes.addFlashAttribute("notification",updateNotificationResponse);
            redirectAttributes.addFlashAttribute("notificationId",id);
            return "redirect:/updateNotification/" + id;
        }
        notificationService.updateNotification(id, request);
        return "redirect:/notification";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/deleteNotification/{id}")
    public String deleteNotification(@PathVariable("id") Long id) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        if (notificationResponse.isPublished()) {
            return "redirect:/notification/" + id;
        }
        notificationService.deleteNotification(id);
        return "redirect:/notification";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
