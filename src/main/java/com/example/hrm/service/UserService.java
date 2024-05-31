package com.example.hrm.service;

import com.example.hrm.dto.request.ChangePasswordRequest;
import com.example.hrm.dto.request.ForgotPasswordRequest;
import com.example.hrm.dto.request.UpdateUserRequest;
import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;

public interface UserService {
    User createUser(UserRequest userRequest);

    void updateUser(Long id, UpdateUserRequest userRequest);

    void deleteUser(Long id);

    UserResponse findById(Long id);

    Page<UserResponse> listUserfindByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword);

    Page<UserResponse> listUserActiveFindByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword);

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
