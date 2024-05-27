package com.example.hrm.controller.user;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.NotificationResponse;
import com.example.hrm.entity.User;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
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
        User userLoggedIn = myUserDetails.getUser();
        Long userId = userLoggedIn.getId();
        LocalTime checkinTime = attendanceService.getCheckinTimeById(localDate, userId);
        LocalTime checkoutTime = attendanceService.getCheckoutTimeById(localDate, userId);
        if (checkinTime != null) {
            model.addAttribute("checkinTime", checkinTime);
        }
        if (checkoutTime != null) {
            model.addAttribute("checkoutTime", checkoutTime);
        }
        List<AttendanceResponse> listAttendanceByWeek = attendanceService.getAttendanceByWeek(localDate, userId);
        model.addAttribute("listAttendance", listAttendanceByWeek);
        List<NotificationResponse> listNotification = notificationService.getTenPublishedNotification();
        model.addAttribute("listNotification", listNotification);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
