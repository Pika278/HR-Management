package com.example.hrm.controller.admin;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

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
                return "redirect:/admin/pushNotification";
            }
        }
        notificationService.pushNotification(request);
        return "redirect:/notification/page/1?keyword";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/updateNotification/{id}")
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
            return "redirect:/admin/updateNotification/" + id;
        }
        notificationService.updateNotification(id, request);
        return "redirect:/notification/page/1?keyword";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/deleteNotification/{id}")
    public String deleteNotification(@PathVariable("id") Long id) {
        NotificationResponse notificationResponse = notificationService.findNotificationById(id);
        if (notificationResponse.isPublished()) {
            return "redirect:/notification/" + id;
        }
        notificationService.deleteNotification(id);
        return "redirect:/notification/page/1?keyword";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
