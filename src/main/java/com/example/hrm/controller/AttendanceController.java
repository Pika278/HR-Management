package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final UserMapper userMapper;
    private static final int PAGE_SIZE = 7;
    private static final String SORT_BY = "date";

    @PostMapping("/checkin")
    public String checkin(Model model) {
        attendanceService.checkin();
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(Model model) {
        attendanceService.checkout();
        return "redirect:/";
    }

    @GetMapping("/timesheet/{numPage}")
    public String timesheetByMonth(@PathVariable(name = "numPage") int pageNum, @RequestParam(value = "monthYear", required = false) String monthYear, Model model) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = customUserDetails.getUser().getId();
        Page<AttendanceResponse> page;
        Integer month = null;
        Integer year = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            String[] parts = monthYear.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            page = attendanceService.getAttendanceByMonth(pageNum,PAGE_SIZE,SORT_BY,userId,month,year);
        }
        else {
            page = attendanceService.getAttendanceByCurrentMonth(pageNum,PAGE_SIZE,SORT_BY,userId);
        }
        List<AttendanceResponse> listAttendance = page.getContent();
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("pageSize",PAGE_SIZE);
        model.addAttribute("sortBy",SORT_BY);
        model.addAttribute("listAttendance",listAttendance);
        model.addAttribute("currentMonthYear",monthYear);
        return "timesheet";
    }
}
