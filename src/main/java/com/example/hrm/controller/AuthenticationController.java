package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.service.AttendanceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class AuthenticationController {
    private final AttendanceService attendanceService;

    public AuthenticationController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/")
    public String home(Model model) {
//        LocalDate date = LocalDate.now();
//        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = myUserDetails.getUser().getId();
//        LocalTime checkinTime = attendanceService.getCheckinTimeById(date,userId);
//        LocalTime checkoutTime = attendanceService.getCheckoutTimeById(date,userId);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        if(checkinTime != null) {
//            String checkinTimeString = formatter.format(checkinTime);
//            model.addAttribute("checkinTime",checkinTimeString);
//        }
//        if (checkoutTime != null) {
//            String checkoutTimeString = formatter.format(checkoutTime);
//            model.addAttribute("checkoutTime",checkoutTimeString);
//        }
        return "home";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
