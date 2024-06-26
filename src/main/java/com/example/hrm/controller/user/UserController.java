package com.example.hrm.controller.user;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.request.*;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.Role;
import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.exception.AppException;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.UserService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {
    private static final int PAGE_SIZE = 10;
    private static final String SORT_BY_ID = "id";
    private final UserService userService;
    private final DepartmentService departmentService;
    private final VerifyTokenService verifyTokenService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/list/{numPage}")
    public String listUser(@RequestParam(value = "keyword", required = false) String keyword, @PathVariable(name = "numPage") int pageNum, Model model) {
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userLoggedInId = myUserDetails.getUser().getId();
        model.addAttribute("userLoginId", userLoggedInId);
        UserResponse userResponse = userService.findById(userLoggedInId);
        Page<UserResponse> page;
        if (userResponse.getRole() == Role.ADMIN) {
            page = userService.listUserfindByKeywordPaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);
        } else {
            page = userService.listUserActiveFindByKeywordPaging(pageNum, PAGE_SIZE, SORT_BY_ID, keyword);

        }
        List<UserResponse> listUsers = page.getContent();
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("sortBy", SORT_BY_ID);
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("keyword", keyword);
        return "list_user";
    }

    @GetMapping("/activation")
    public String activation(@RequestParam("token") String token, Model model) {
        VerifyToken verifyToken = verifyTokenService.findByToken(token);
        if (verifyToken == null) {
            model.addAttribute("message", "Mã xác thực không khả dụng");
        } else {
            User user = verifyToken.getUser();
            if (!user.isIs_active()) {
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                //check if token is expired
                if (verifyToken.getExpireDate().before(currentTime)) {
                    model.addAttribute("message", "Mã xác thực đã hết hạn");
                } else {
                    model.addAttribute("token", token);
                    model.addAttribute("passwordRequest", new PasswordRequest());
                    return "create_password";
                }
            } else {
                model.addAttribute("message", "Tài khoản đã kích hoạt");
            }
        }

        return "activation";
    }

    @PostMapping("/createPassword")
    public String createPassword(@Valid @ModelAttribute PasswordRequest passwordRequest, BindingResult bindingResult, Model model, HttpServletRequest request) {
        String token = request.getParameter("token");
        if (!passwordRequest.getPassword().equals(passwordRequest.getConfirmPassword())) {
            bindingResult.addError(new FieldError("passwordRequest", "confirmPassword", "Vui lòng nhập mật khẩu giống với ở trên"));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordRequest", passwordRequest);
            return "create_password";
        }

        VerifyToken verifyToken = verifyTokenService.findByToken(token);
        if (verifyToken != null) {
            User user = verifyToken.getUser();
            userService.createPassword(user, passwordRequest.getPassword());
            return "redirect:/login?create_password_success";
        }
        return "create_password";
    }

    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userLoggedInId = myUserDetails.getUser().getId();
        model.addAttribute("userLoginId", userLoggedInId);
        UserResponse userResponse = userService.findById(userLoggedInId);
        model.addAttribute("user", userResponse);
        List<Department> listDepartment = departmentService.getAllDepartment();
        model.addAttribute("listDepartment", listDepartment);
        return "user_detail";
    }

    @GetMapping("/forgotPasswordForm")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPassword", new ForgotPasswordRequest());
        return "forgot_password_form";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@Valid @ModelAttribute("forgotPassword") ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("forgotPasswordRequest", forgotPasswordRequest);
            return "forgot_password_form";
        } else {
            UserResponse userResponse = userService.findByEmail(forgotPasswordRequest.getEmail());
            if (userResponse == null) {
                return "redirect:/user/forgotPasswordForm?not_found";
            } else if (!userResponse.isIs_active()) {
                return "redirect:/user/forgotPasswordForm?not_active";
            } else {
                userService.forgotPassword(forgotPasswordRequest);
                return "redirect:/login?send_email";
            }
        }
    }

    @GetMapping("/resetPasswordForm")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        VerifyToken verifyToken = verifyTokenService.findByToken(token);
        if (verifyToken == null) {
            return "invalid_token";
        } else {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (verifyToken.getExpireDate().before(currentTime)) {
                return "invalid_token";
            } else {
                model.addAttribute("token", token);
                model.addAttribute("passwordRequest", new PasswordRequest());
                return "reset_password_form";
            }
        }
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute PasswordRequest passwordRequest, BindingResult bindingResult, Model model, HttpServletRequest request) {
        String token = request.getParameter("token");
        if (!passwordRequest.getPassword().equals(passwordRequest.getConfirmPassword())) {
            bindingResult.addError(new FieldError("passwordRequest", "confirmPassword", "Vui lòng nhập mật khẩu giống với ở trên"));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordRequest", passwordRequest);
            return "reset_password_form";
        }
        VerifyToken verifyToken = verifyTokenService.findByToken(token);
        if (verifyToken != null) {
            User user = verifyToken.getUser();
            userService.createPassword(user, passwordRequest.getPassword());
            return "redirect:/login?reset_password_success";
        }
        return "redirect:/login";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "change_password";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest, BindingResult bindingResult, Model model) {
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = myUserDetails.getUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordRequest", changePasswordRequest);
            return "change_password";
        }
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            bindingResult.addError(new FieldError("changePasswordRequest", "oldPassword", "Sai mật khẩu"));
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            bindingResult.addError(new FieldError("changePasswordRequest", "confirmPassword", "Vui lòng nhập mật khẩu giống với ở trên"));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordRequest", changePasswordRequest);
            return "change_password";
        }
        userService.changePassword(changePasswordRequest, user);
        return "redirect:/login?change_password_success";
    }

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, Model model) {
        model.addAttribute("errorMessage", e.getErrorMessage().getMessage());
        return "error";
    }
}
