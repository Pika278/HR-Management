package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.request.AddAttendanceRequest;
import com.example.hrm.dto.request.UpdateAttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.exception.AppException;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final UserMapper userMapper;
    private static final int PAGE_SIZE = 7;
    private static final String SORT_BY_DATE = "date";

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
            page = attendanceService.getAttendanceByMonth(pageNum,PAGE_SIZE,SORT_BY_DATE,userId,month,year);
        }
        else {
            page = attendanceService.getAttendanceByCurrentMonth(pageNum,PAGE_SIZE,SORT_BY_DATE,userId);
        }
        List<AttendanceResponse> listAttendance = page.getContent();
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("pageSize",PAGE_SIZE);
        model.addAttribute("sortBy",SORT_BY_DATE);
        model.addAttribute("listAttendance",listAttendance);
        model.addAttribute("currentMonthYear",monthYear);
        UserResponse userResponse = userService.findById(userId);
        if(userResponse.getRole() == Role.ADMIN) {
            model.addAttribute("userId",userId);
        }
        return "timesheet";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/timesheet/{id}/{numPage}")
    public String userTimesheetByMonth(@PathVariable(name = "id") Long userId, @PathVariable(name = "numPage") int pageNum, @RequestParam(value = "monthYear", required = false) String monthYear, Model model) {
        Page<AttendanceResponse> page;
        Integer month = null;
        Integer year = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            String[] parts = monthYear.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            page = attendanceService.getAttendanceByMonth(pageNum,PAGE_SIZE,SORT_BY_DATE,userId,month,year);
        }
        else {
            page = attendanceService.getAttendanceByCurrentMonth(pageNum,PAGE_SIZE,SORT_BY_DATE,userId);
        }
        List<AttendanceResponse> listAttendance = page.getContent();
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("pageSize",PAGE_SIZE);
        model.addAttribute("sortBy",SORT_BY_DATE);
        model.addAttribute("listAttendance",listAttendance);
        model.addAttribute("currentMonthYear",monthYear);
        model.addAttribute("userId",userId);
        return "timesheet";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/updateAttendance/{id}")
    public String updateAttendanceForm(@PathVariable Long id, Model model) {
        AttendanceResponse attendanceResponse = attendanceService.findAttendanceById(id);
        model.addAttribute("attendance",attendanceResponse);
        return "update_attendance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateAttendance/{id}")
    public String updateAttendance(@PathVariable Long id, @Valid @ModelAttribute("attendance") UpdateAttendanceRequest request, BindingResult bindingResult, Model model) {
        AttendanceResponse attendanceResponse = attendanceService.findAttendanceById(id);
        if(request.getCheckoutTime().isBefore(request.getCheckinTime())) {
            bindingResult.addError(new FieldError("attendance","checkoutTime","Giờ checkout phải lớn hơn giờ checkin"));
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("attendance",request);
            return "update_attendance";
        }
        attendanceService.updateAttendance(id,request);
        return "redirect:/timesheet/" + attendanceResponse.getUserId() + "/1";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/addAttendance/{id}")
    public String addAttendanceForm(@PathVariable("id") Long userId, Model model) {
        AddAttendanceRequest attendanceRequest = new AddAttendanceRequest();
        model.addAttribute("attendanceRequest",attendanceRequest);
        model.addAttribute("userId",userId);
        return "add_attendance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addAttendance/{id}")
    public  String addAttendance(@PathVariable("id") Long userId, @Valid @ModelAttribute("attendanceRequest") AddAttendanceRequest attendanceRequest, BindingResult bindingResult, Model model) {
        if(attendanceService.getAttendanceByDateUser(attendanceRequest.getDate(),userId) != null) {
            bindingResult.addError(new FieldError("attendanceRequest","date","Chấm công đã tồn tại"));
        }
        if(attendanceRequest.getCheckoutTime().isBefore(attendanceRequest.getCheckinTime())) {
            bindingResult.addError(new FieldError("attendanceRequest","checkoutTime","Giờ checkout phải lớn hơn giờ checkin"));
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("attendanceRequest",attendanceRequest);
            model.addAttribute("userId",userId);
            return "add_attendance";
        }
        attendanceService.addAttendance(userId,attendanceRequest);
        return "redirect:/timesheet/" + userId + "/1";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}