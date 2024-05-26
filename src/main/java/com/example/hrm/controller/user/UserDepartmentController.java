package com.example.hrm.controller.user;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserDepartmentController {
    private static final int PAGE_SIZE = 10;
    private static final String SORT_BY_ID = "id";
    private final DepartmentService departmentService;
    private final UserService userService;

    @GetMapping("/department/{numPage}")
    public String listDepartment(@RequestParam("keyword") String keyword, @PathVariable(name = "numPage") int pageNum, Model model) {
        if (!model.containsAttribute("departmentRequest")) {
            DepartmentRequest departmentRequest = new DepartmentRequest();
            model.addAttribute("departmentRequest", departmentRequest);
        } else {
            model.addAttribute("isShowAddModal", true);
        }
        if (!model.containsAttribute("updateDepartment")) {
            DepartmentRequest updateDepartment = new DepartmentRequest();
            model.addAttribute("updateDepartment", updateDepartment);
        } else {
            model.addAttribute("isShowUpdateModal", true);
        }
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse userResponse = userService.findById(myUserDetails.getUser().getId());
        Page<DepartmentResponse> page;
        if (userResponse.getRole() == Role.ADMIN) {
            page = departmentService.findByNamePaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);
        } else {
            page = departmentService.findByNameActivePaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);
        }

        List<DepartmentResponse> listDept = page.getContent();
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("sortBy", SORT_BY_ID);
        model.addAttribute("listDept", listDept);
        model.addAttribute("keyword", keyword);
        return "list_department";
    }

    @GetMapping("/department/user/{departmentId}/page/{numPage}")
    public String listDepartmentUser(@PathVariable("departmentId") Long departmentId, @PathVariable(name = "numPage") int pageNum, Model model) {
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse userResponse = userService.findById(myUserDetails.getUser().getId());
        Page<UserResponse> page;
        if (userResponse.getRole() == Role.ADMIN) {
            page = userService.listDepartmentUserPaging(pageNum, PAGE_SIZE, SORT_BY_ID, departmentId);
        } else {
            page = userService.listDepartmentUserActivePaging(pageNum, PAGE_SIZE, SORT_BY_ID, departmentId);
        }
        List<UserResponse> listUsers = page.getContent();
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("sortBy", SORT_BY_ID);
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("departmentId", departmentId);
        return "list_department_user";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
