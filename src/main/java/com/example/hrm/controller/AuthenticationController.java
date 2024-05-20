package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.configuration.CustomUserDetailsService;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.service.AttendanceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AuthenticationController {
    private final AttendanceService attendanceService;

    public AuthenticationController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

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
        return "home";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
