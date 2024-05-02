package com.example.hrm.controller;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/department")
    public String listDepartment(Model model) {

        DepartmentRequest departmentRequest = new DepartmentRequest();
        model.addAttribute("department",departmentRequest);
        List<Department> listDept = departmentService.getAllDepartment();
        model.addAttribute("listDept",listDept);
        return "list_department";
    }

    @PostMapping("/addDepartment")
    public String addDepartment(@ModelAttribute("department") DepartmentRequest departmentRequest, Model model) {
        departmentService.createDepartment(departmentRequest);
        List<Department> listDept = departmentService.getAllDepartment();
        model.addAttribute("listDept",listDept);
        return "redirect:department";
    }

    @PostMapping("/updateDepartment/{id}")
    public String updateDepartment(@ModelAttribute("department") DepartmentRequest departmentRequest, Model model, @PathVariable Long id) {

        departmentService.updateDepartment(id,departmentRequest);
        List<Department> listDept = departmentService.getAllDepartment();
        model.addAttribute("listDept",listDept);
        return "redirect:/department";
    }
    @PostMapping("/deleteDepartment/{id}")
    public String deleteDepartment(Model model, @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        List<Department> listDept = departmentService.getAllDepartment();
        model.addAttribute("listDept",listDept);
        return "redirect:/department";
    }
}
