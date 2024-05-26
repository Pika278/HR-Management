package com.example.hrm.controller.admin;

import com.example.hrm.dto.request.*;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final DepartmentService departmentService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add")
    public String addUserForm(Model model) {
        if(!model.containsAttribute("user")) {
            UserRequest userRequest = new UserRequest();
            model.addAttribute("user", userRequest);
        }
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "register";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addUser(
            @Valid @ModelAttribute("user") UserRequest userRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (userService.emailExists(userRequest.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Email đã tồn tại"));
        }
        if (userService.citizenIdExists(userRequest.getCitizenId())) {
            bindingResult.addError(new FieldError("user", "citizenId", "CCCD đã tồn tại"));
        }
        if (bindingResult.hasErrors()) {
            for(FieldError error: bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute(error.getField(), error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("user", userRequest);
            return "redirect:/admin/add";
        } else {
            userService.createUser(userRequest);
            return "redirect:/user/list/1?keyword=";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/userDetail/{id}")
    public String getUserDetail(@PathVariable("id") Long id, Model model) {
        UserResponse userResponse = userService.findById(id);
        model.addAttribute("user", userResponse);
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "user_detail";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("update/{id}")
    public String updateUserForm(@PathVariable Long id, Model model) {
        if(!model.containsAttribute("user")) {
            UserResponse userResponse = userService.findById(id);
            model.addAttribute("user", userResponse);
        }
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "update_user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") UpdateUserRequest userRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        UserResponse userResponse = userService.findById(id);
        if (!userRequest.getCitizenId().equals(userResponse.getCitizenId()) && userService.citizenIdExists(userRequest.getCitizenId())) {
            bindingResult.addError(new FieldError("user", "citizenId", "CCCD đã tồn tại"));
        }
        if (bindingResult.hasErrors()) {
            for(FieldError error: bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute(error.getField(), error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("user", userRequest);
            return "redirect:/admin/update/" + id;
        }
        userService.updateUser(id, userRequest);
        return "redirect:/admin/userDetail/" + id;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/changeActive/{id}")
    public String changeActive(@PathVariable("id") Long id) {
        userService.changeActive(id);
        return "redirect:/user/list/1?keyword=";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/user/list/1?keyword=";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
