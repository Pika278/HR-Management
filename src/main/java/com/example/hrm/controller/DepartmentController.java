package com.example.hrm.controller;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String listDepartment(@RequestParam("keyword") String keyword, Model model) {
        if(!model.containsAttribute("department")) {
            DepartmentRequest departmentRequest = new DepartmentRequest();
            model.addAttribute("department",departmentRequest);
        }
        else {
            model.addAttribute("isShowAddModal", true);
        }
        if(!model.containsAttribute("updateDepartment")) {
            DepartmentRequest updateDepartment = new DepartmentRequest();
            model.addAttribute("updateDepartment",updateDepartment);
        }
        else {
            model.addAttribute("isShowUpdateModal", true);
        }
//        List<DepartmentResponse> listDept = departmentService.getAllDepartment();
        List<DepartmentResponse> listDept = departmentService.findByName(keyword);
        model.addAttribute("keyword",keyword);
        model.addAttribute("listDept",listDept);
        return "list_department";
    }

    @PostMapping("/addDepartment")
    public String addDepartment(@Valid @ModelAttribute("department") DepartmentRequest departmentRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("department","name","Phòng đã tồn tại"));
        }
        if(!bindingResult.hasErrors()) {
            departmentService.createDepartment(departmentRequest);
            return "redirect:/department";
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
        redirectAttributes.addFlashAttribute("department", departmentRequest);
        return "redirect:/department";
    }

    @PostMapping("/updateDepartment/{id}")
    public String updateDepartment(@ModelAttribute("updateDepartment") DepartmentRequest departmentRequest, Model model, @PathVariable Long id, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(departmentService.departmentExists(departmentRequest)) {
            bindingResult.addError(new FieldError("updateDepartment","name","Phòng đã tồn tại"));
            model.addAttribute("isShowUpdateModal", true);
            model.addAttribute("departmentId",id);
        }
        if(!bindingResult.hasErrors()) {
            departmentService.updateDepartment(id,departmentRequest);
            return "redirect:/department";
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateDepartment", bindingResult);
        redirectAttributes.addFlashAttribute("updateDepartment", departmentRequest);
        redirectAttributes.addFlashAttribute("departmentId",id);
        return "redirect:/department";


    }
    @PostMapping("/deleteDepartment/{id}")
    public String deleteDepartment(Model model, @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        List<DepartmentResponse> listDept = departmentService.getAllDepartment();
        model.addAttribute("listDept",listDept);
        return "redirect:/department";
    }
}
