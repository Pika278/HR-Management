package com.example.hrm.service.impl;

import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.User;
import com.example.hrm.mapper.DepartmentMapper;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.repository.UserRepository;
import com.example.hrm.repository.criteria.UserCriteria;

import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.EmailService;
import com.example.hrm.service.UserService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VerifyTokenService verifyTokenService;
    private final EmailService emailService;
    private final UserCriteria userCriteria;
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, VerifyTokenService verifyTokenService, EmailService emailService, UserCriteria userCriteria, DepartmentService departmentService, DepartmentMapper  departmentMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.verifyTokenService = verifyTokenService;
        this.emailService = emailService;
        this.userCriteria = userCriteria;
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void createUser(UserRequest userRequest) {
        User user = userMapper.userRequestToUser(userRequest);
        user.setIs_active(false);
//        Long departmentId = departmentService.findById(userRequest.getDepartmentId()).getId();
        DepartmentResponse departmentResponse = departmentService.findById(userRequest.getDepartmentId());
        Department department = departmentMapper.departmentResponsetoDepartment(departmentResponse);
        user.setDepartment(department);
        Optional<User> saved = Optional.of(saveUser(user));
        saved.ifPresent(u -> {
            try {
                String token = UUID.randomUUID().toString();
                verifyTokenService.save(user,token);
                //send verify email
                emailService.sendHTMLMail(u);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void updateUser(Long id, UserRequest userRequest) {

    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public UserResponse findById(Long id) {
        return null;
    }

//    @Override
//    public List<UserResponse> getAllUser() {
//        return List.of();
//    }

    @Override
    public Page<UserResponse> findByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).ascending());
        Page<User> list = userRepository.findUser(pageable,keyword);
        Page<UserResponse> responsePage = list.map(userMapper::toUserResponse);

        return responsePage;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean citizenIdExists(String citizenId) {
        return userRepository.existsByCitizenId(citizenId);

    }

//    @Override
//    public boolean userExists(UserRequest userRequest) {
//        return userRepository.existsByEmail(userRequest.getEmail());
//    }
}
