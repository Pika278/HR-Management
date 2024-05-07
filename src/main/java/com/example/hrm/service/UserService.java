package com.example.hrm.service;

import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    void createUser(UserRequest userRequest);
    void updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    UserResponse findById(Long id);
    Page<UserResponse> findByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword);
    boolean emailExists(String email);
    boolean citizenIdExists(String citizenId);

}
