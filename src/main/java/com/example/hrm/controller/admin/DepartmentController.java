package com.example.hrm.controller.admin;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("admin/department")
@Controller
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addDepartment(@Valid @ModelAttribute("departmentRequest") DepartmentRequest departmentRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("departmentRequest", "name", "Phòng đã tồn tại"));
        }
        if (!bindingResult.hasErrors()) {
            departmentService.createDepartment(departmentRequest);
            return "redirect:/department/1?keyword=";
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.departmentRequest", bindingResult);
        redirectAttributes.addFlashAttribute("departmentRequest", departmentRequest);
        return "redirect:/department/1?keyword=";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateDepartment(@Valid @ModelAttribute("updateDepartment") DepartmentRequest departmentRequest, @PathVariable Long id, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("updateDepartment", "name", "Phòng đã tồn tại"));
        }
        if (!bindingResult.hasErrors()) {
            departmentService.updateDepartment(id, departmentRequest);
            return "redirect:/department/1?keyword=";

        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateDepartment", bindingResult);
        redirectAttributes.addFlashAttribute("updateDepartment", departmentRequest);
        return "redirect:/department/1?keyword=";



    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteDepartment(Model model, @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/department/1?keyword=";

    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
