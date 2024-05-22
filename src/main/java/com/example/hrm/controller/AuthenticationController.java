package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.configuration.CustomUserDetailsService;
import com.example.hrm.dto.request.AddNotificationRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.entity.Notification;
import com.example.hrm.entity.User;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;


    @GetMapping("/")
    public String home(Model model) {
        LocalDate localDate = LocalDate.now();
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        LocalTime checkinTime = attendanceService.getCheckinTimeById(localDate,userId);
        LocalTime checkoutTime = attendanceService.getCheckoutTimeById(localDate,userId);
        if(checkinTime != null) {
            model.addAttribute("checkinTime",checkinTime);
        }
        if (checkoutTime != null) {
            model.addAttribute("checkoutTime",checkoutTime);
        }
        List<AttendanceResponse> listAttendanceByWeek = attendanceService.getAttendanceByWeek(localDate,userId);
        model.addAttribute("listAttendance",listAttendanceByWeek);
        List<NotificationResponse> listNotification = notificationService.getAllNotification();
        model.addAttribute("listNotification",listNotification);
        return "home";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
