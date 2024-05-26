package com.example.hrm.controller;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/department")
@Controller
@RequiredArgsConstructor
public class DepartmentController {
    private static final int PAGE_SIZE = 10;
    private static final String SORT_BY_ID = "id";
    private final DepartmentService departmentService;
    private final HttpSession session;
    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/{numPage}")
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
            long id = (long) session.getAttribute("departmentId");
            model.addAttribute("departmentId", id);
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
        session.setAttribute("url", "department/" + pageNum + "?keyword=" + keyword);
        return "list_department";
    }

    @PostMapping("/add")
    public String addDepartment(@Valid @ModelAttribute("departmentRequest") DepartmentRequest departmentRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("departmentRequest", "name", "Phòng đã tồn tại"));
        }
        if (!bindingResult.hasErrors()) {
            departmentService.createDepartment(departmentRequest);
            String url = (String) session.getAttribute("url");
            return "redirect:/" + url;
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.departmentRequest", bindingResult);
        redirectAttributes.addFlashAttribute("departmentRequest", departmentRequest);
        String url = (String) session.getAttribute("url");
        return "redirect:/" + url;
    }

    @PostMapping("/update/{id}")
    public String updateDepartment(@Valid @ModelAttribute("updateDepartment") DepartmentRequest departmentRequest, @PathVariable Long id, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("updateDepartment", "name", "Phòng đã tồn tại"));
            session.setAttribute("departmentId", id);
        }
        if (!bindingResult.hasErrors()) {
            departmentService.updateDepartment(id, departmentRequest);
            session.setAttribute("departmentId", id);
            String url = (String) session.getAttribute("url");
            return "redirect:/" + url;
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateDepartment", bindingResult);
        redirectAttributes.addFlashAttribute("updateDepartment", departmentRequest);
        session.setAttribute("departmentId", id);
        String url = (String) session.getAttribute("url");
        return "redirect:/" + url;


    }

    @PostMapping("/delete/{id}")
    public String deleteDepartment(Model model, @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        String url = (String) session.getAttribute("url");
        return "redirect:/" + url;
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
