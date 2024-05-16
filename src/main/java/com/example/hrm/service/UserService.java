package com.example.hrm.service;

import com.example.hrm.dto.request.ChangePasswordRequest;
import com.example.hrm.dto.request.ForgotPasswordRequest;
import com.example.hrm.dto.request.UpdateUserRequest;
import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.User;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    void createUser(UserRequest userRequest);
    void updateUser(Long id, UpdateUserRequest userRequest);
    void deleteUser(Long id);
    UserResponse findById(Long id);
    Page<UserResponse> findByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword);
    Page<UserResponse> findUserActiveByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword);
    Page<UserResponse> listDepartmentUserPaging(int pageNumber, int pageSize, String sortBy, Long departmentId);
    Page<UserResponse> listDepartmentUserActivePaging(int pageNumber, int pageSize, String sortBy, Long departmentId);
    boolean emailExists(String email);
    boolean citizenIdExists(String citizenId);
    void changeActive(Long id);
    void createPassword(User user, String password);
    UserResponse findByEmail(String email);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest, User user);
}
