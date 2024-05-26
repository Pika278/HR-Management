package com.example.hrm.controller.admin;

import com.example.hrm.dto.request.AddAttendanceRequest;
import com.example.hrm.dto.request.UpdateAttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.exception.AppException;
import com.example.hrm.mapper.AttendanceMapper;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.UserService;
import com.example.hrm.utils.DateTimeHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class AttendanceController {
    private static final int PAGE_SIZE = 7;
    private static final String SORT_BY_DATE = "date";
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final AttendanceMapper attendanceMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/timesheet/user/{id}/page/{numPage}")
    public String userTimesheetByMonth(@PathVariable(name = "id") Long userId, @PathVariable(name = "numPage") int pageNum, @RequestParam(value = "monthYear", required = false) String monthYear, Model model) {
        Page<AttendanceResponse> page;
        Integer month = null;
        Integer year = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            String[] parts = monthYear.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            page = attendanceService.getAttendanceByMonth(pageNum, PAGE_SIZE, SORT_BY_DATE, userId, month, year);
        } else {
            page = attendanceService.getAttendanceByCurrentMonth(pageNum, PAGE_SIZE, SORT_BY_DATE, userId);
        }
        List<AttendanceResponse> listAttendance = page.getContent();
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("sortBy", SORT_BY_DATE);
        model.addAttribute("listAttendance", listAttendance);
        model.addAttribute("currentMonthYear", monthYear);
        model.addAttribute("userId", userId);
        return "timesheet";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/updateAttendance/{id}")
    public String updateAttendanceForm(@PathVariable Long id, Model model) {
        if(!model.containsAttribute("attendance")) {
            AttendanceResponse attendanceResponse = attendanceService.findAttendanceById(id);
            model.addAttribute("attendance", attendanceResponse);
            model.addAttribute("attendanceId", id);
        }
        return "update_attendance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateAttendance/{id}")
    public String updateAttendance(@PathVariable Long id, @Valid @ModelAttribute("attendance") UpdateAttendanceRequest request,
                                   BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        AttendanceResponse attendanceResponse = attendanceService.findAttendanceById(id);
        if (request.getCheckoutTime() != null && request.getCheckoutTime().isBefore(request.getCheckinTime())) {
            bindingResult.addError(new FieldError("attendance", "checkoutTime", "Giờ checkout phải lớn hơn giờ checkin"));
        }
        if (attendanceResponse.getDate().equals(DateTimeHelper.formatDate(LocalDate.now()))){
            if (request.getCheckoutTime() != null && !DateTimeHelper.formatTime(request.getCheckoutTime()).equals(attendanceResponse.getCheckoutTime()) && request.getCheckoutTime().isAfter(LocalTime.now())) {
                bindingResult.addError(new FieldError("attendance", "checkoutTime", "Không được sửa thời gian ở tương lai"));
            }
            if (request.getCheckinTime() != null && !DateTimeHelper.formatTime(request.getCheckinTime()).equals(attendanceResponse.getCheckinTime()) && request.getCheckinTime().isAfter(LocalTime.now())) {
                bindingResult.addError(new FieldError("attendance", "checkinTime", "Không được sửa thời gian ở tương lai"));
            }
        }
        if (bindingResult.hasErrors()) {
            for(FieldError error: bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute(error.getField(), error.getDefaultMessage());
            }
            AttendanceResponse response = attendanceMapper.attendanceRequestToUpdateAttendanceResponse(request);
            redirectAttributes.addFlashAttribute("attendance", response);
            redirectAttributes.addFlashAttribute("attendanceId",id);
            return "redirect:/updateAttendance/" + id;
        }
        attendanceService.updateAttendance(id, request);
        return "redirect:/timesheet/" + attendanceResponse.getUserId() + "/1";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/addAttendance/{id}")
    public String addAttendanceForm(@PathVariable("id") Long userId, Model model) {
        if(!model.containsAttribute("attendanceRequest")) {
            AddAttendanceRequest attendanceRequest = new AddAttendanceRequest();
            model.addAttribute("attendanceRequest", attendanceRequest);
            model.addAttribute("userId", userId);
        }
        return "add_attendance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addAttendance/{id}")
    public String addAttendance(@PathVariable("id") Long userId, @Valid @ModelAttribute("attendanceRequest") AddAttendanceRequest attendanceRequest,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (attendanceService.getAttendanceByDateUser(attendanceRequest.getDate(), userId) != null) {
            bindingResult.addError(new FieldError("attendanceRequest", "date", "Chấm công đã tồn tại"));
        }
        if (attendanceRequest.getCheckoutTime().isBefore(attendanceRequest.getCheckinTime())) {
            bindingResult.addError(new FieldError("attendanceRequest", "checkoutTime", "Giờ checkout phải lớn hơn giờ checkin"));
        }
        if (attendanceRequest.getDate().equals(LocalDate.now())) {
            if (attendanceRequest.getCheckoutTime() != null && attendanceRequest.getCheckoutTime().isAfter(LocalTime.now())) {
                bindingResult.addError(new FieldError("attendance", "checkoutTime", "Không được sửa thời gian ở tương lai"));
            }
            if (attendanceRequest.getCheckinTime() != null && attendanceRequest.getCheckinTime().isAfter(LocalTime.now())) {
                bindingResult.addError(new FieldError("attendance", "checkinTime", "Không được sửa thời gian ở tương lai"));
            }
        }
        if (bindingResult.hasErrors()) {
            for(FieldError error: bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute(error.getField(),error.getDefaultMessage());
            }
            AttendanceResponse response = attendanceMapper.attendanceRequestToAddAttendanceResponse(attendanceRequest);
            redirectAttributes.addFlashAttribute("attendanceRequest", response);
            redirectAttributes.addFlashAttribute("userId",userId);
            return "redirect:/addAttendance/" + userId;
        }
        attendanceService.addAttendance(userId, attendanceRequest);
        return "redirect:/timesheet/" + userId + "/1";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
