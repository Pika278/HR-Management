package com.example.hrm.controller;

import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.UserService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;
@RequestMapping("/user")
@Controller
public class UserController {
    private final UserService userService;
    private final DepartmentService departmentService;
    private final HttpSession session;
    private final VerifyTokenService verifyTokenService;

    public UserController(UserService userService, DepartmentService departmentService, HttpSession session, VerifyTokenService verifyTokenService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.session = session;
        this.verifyTokenService = verifyTokenService;
    }

    @GetMapping("/list/{numPage}")
    public String listUser(@RequestParam("keyword") String keyword, @PathVariable(name = "numPage") int pageNum, Model model) {
        int pageSize = 5;
        String sortBy="id";
        Page<UserResponse> page = userService.findByKeywordPaging(pageNum,pageSize,sortBy,keyword);
        List<UserResponse> listUsers = page.getContent();
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("listUsers",listUsers);
        model.addAttribute("keyword",keyword);
        session.setAttribute("url","user/list/" + pageNum + "?keyword=" + keyword);
        return "list_user";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add")
    public String addUserForm(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("user",userRequest);
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment",listDepartment);
        return "register";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") UserRequest userRequest, BindingResult bindingResult, Model model) {
        if(userService.emailExists(userRequest.getEmail())) {
            bindingResult.addError(new FieldError("user","email","Email đã tồn tại"));
        }
        if(userService.citizenIdExists(userRequest.getCitizenId())) {
            bindingResult.addError(new FieldError("user","citizenId","CCCD đã tồn tại"));
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("user",userRequest);
            List<Department> listDepartment = departmentService.getAllDepartment();
            model.addAttribute("listDepartment", listDepartment);
            return "register";
        }
        else {
            userService.createUser(userRequest);
            String url = (String) session.getAttribute("url");
            return "redirect:/" + url;
        }
    }

    @GetMapping("/activation")
    public String activation(@RequestParam("token") String token, Model model) {
        VerifyToken verifyToken = verifyTokenService.findByToken(token);
        if(verifyToken == null) {
            model.addAttribute("message","Mã xác thực không khả dụng");
        }
        else {
            User user = verifyToken.getUser();
            if(!user.isIs_active()) {
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                //check if token is expired
                if(verifyToken.getExpireDate().before(currentTime)) {
                    model.addAttribute("message", "Mã xác thực đã hết hạn");
                }
                else {
                    user.setIs_active(true);
                    userService.saveUser(user);
                    model.addAttribute("message", "Tài khoản đã được kích hoạt thành công");
                }
            }
            else {
                model.addAttribute("message", "Tài khoản đã kích hoạt");
            }
        }

        return "activation_error";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public String getUserDetail(@PathVariable Long id, Model model) {
        UserResponse userResponse = userService.findById(id);
        model.addAttribute("user",userResponse);
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "user_detail";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("update/{id}")
    public String updateUserForm(@PathVariable Long id, Model model) {
        UserResponse userResponse = userService.findById(id);
        model.addAttribute("user",userResponse);
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "update_user";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") UserRequest userRequest, BindingResult bindingResult, Model model) {
        UserResponse userResponse = userService.findById(id);
        if(!userRequest.getEmail().equals(userResponse.getEmail()) && userService.emailExists(userRequest.getEmail())) {
                bindingResult.addError(new FieldError("user","email","Email đã tồn tại"));
        }
        if(!userRequest.getCitizenId().equals(userResponse.getCitizenId()) && userService.citizenIdExists(userRequest.getCitizenId())) {
            bindingResult.addError(new FieldError("user","citizenId","CCCD đã tồn tại"));
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("user",userRequest);
            List<Department> listDepartment = departmentService.getAllDepartment();
            model.addAttribute("listDepartment", listDepartment);
            return "update_user";
        }
        userService.updateUser(id, userRequest);
        return "redirect:/user/" + id;
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/changeActive/{id}")
    public String changeActive(@PathVariable("id") Long id) {
        userService.changeActive(id);
        String url = (String) session.getAttribute("url");
        return "redirect:/" + url;
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        String url = (String) session.getAttribute("url");
        return "redirect:/" + url;
    }

    @GetMapping("/departmentUser/{departmentId}/{numPage}")
    public String listDepartmentUser(@PathVariable("departmentId") Long departmentId, @PathVariable(name = "numPage") int pageNum, Model model) {
        int pageSize = 5;
        String sortBy="id";
        Page<UserResponse> page = userService.listDepartmentUserPaging(pageNum,pageSize,sortBy,departmentId);
        List<UserResponse> listUsers = page.getContent();
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("listUsers",listUsers);
        session.setAttribute("url","user/departmentUser/"  + departmentId + "/" + pageNum);
        return "list_department_user";
    }
}
