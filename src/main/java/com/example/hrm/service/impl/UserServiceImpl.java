package com.example.hrm.service.impl;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.configuration.CustomUserDetailsService;
import com.example.hrm.dto.request.ChangePasswordRequest;
import com.example.hrm.dto.request.ForgotPasswordRequest;
import com.example.hrm.dto.request.UpdateUserRequest;
import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.User;
import com.example.hrm.entity.VerifyToken;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.mapper.DepartmentMapper;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.repository.UserRepository;
import com.example.hrm.repository.criteria.UserCriteria;

import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.EmailService;
import com.example.hrm.service.UserService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VerifyTokenService verifyTokenService;
    private final EmailService emailService;
    private final UserCriteria userCriteria;
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void createUser(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorMessage.USER_EXISTED);
        }
        User user = userMapper.userRequestToUser(userRequest);
        user.setIs_active(false);
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
                Long quantity = department.getQuantity()+1;
                department.setQuantity(quantity);
                departmentService.saveDepartment(department);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void updateUser(Long id, UpdateUserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            userMapper.updateUser(user,userRequest);
            DepartmentResponse departmentResponse = departmentService.findById(user.getDepartment().getId());
            Department department = departmentMapper.departmentResponsetoDepartment(departmentResponse);
            DepartmentResponse departmentResponse1 = departmentService.findById(userRequest.getDepartmentId());
            Department department1 = departmentMapper.departmentResponsetoDepartment(departmentResponse1);
            user.setDepartment(department1);
            saveUser(user);
            //update department quantity
            department.setQuantity(department.getQuantity()-1);
            department1.setQuantity(department1.getQuantity()+1);
            departmentService.saveDepartment(department);
            departmentService.saveDepartment(department1);
            CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(myUserDetails.getUser().getId().equals(user.getId())) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            VerifyToken verifyToken = verifyTokenService.findByUser(user);
            verifyTokenService.deleteToken(verifyToken);
            userRepository.deleteById(id);
            DepartmentResponse departmentResponse = departmentService.findById(user.getDepartment().getId());
            Department department = departmentMapper.departmentResponsetoDepartment(departmentResponse);
            department.setQuantity(department.getQuantity()-1);
            departmentService.saveDepartment(department);
        }
        else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }

    }

    @Override
    public UserResponse findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        UserResponse userResponse = new UserResponse();
        if(user.isPresent()) {
            userResponse = userMapper.toUserResponse(user.get());
            userResponse.setDepartmentId(user.get().getDepartment().getId());
        }
        return userResponse;
    }


    @Override
    public Page<UserResponse> findByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).ascending());
        Page<User> list = userCriteria.findByKeyword(pageable,keyword);
        Page<UserResponse> responsePage = list.map(userMapper::toUserResponse);

        return responsePage;
    }

    @Override
    public Page<UserResponse> findUserActiveByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).ascending());
        Page<User> list = userCriteria.findUserActiveByKeyword(pageable,keyword);
        Page<UserResponse> responsePage = list.map(userMapper::toUserResponse);
        return responsePage;
    }

    @Override
    public Page<UserResponse> listDepartmentUserPaging(int pageNumber, int pageSize, String sortBy, Long departmentId) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).ascending());
        Page<User> list = userRepository.listDepartmentUser(pageable,departmentId);
        Page<UserResponse> responsePage = list.map(userMapper::toUserResponse);
        return responsePage;
    }

    @Override
    public Page<UserResponse> listDepartmentUserActivePaging(int pageNumber, int pageSize, String sortBy, Long departmentId) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).ascending());
        Page<User> list = userRepository.listDepartmentUserActive(pageable,departmentId);
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

    @Override
    public void changeActive(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIs_active(!user.isIs_active());
            saveUser(user);
        }
        else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public void createPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setIs_active(true);
        VerifyToken token = verifyTokenService.findByUser(user);
        verifyTokenService.deleteToken(token);
        saveUser(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
       User user = userRepository.findByEmail(email).orElse(null);
       return userMapper.toUserResponse(user);
    }

    @Async
    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        UserResponse userResponse = findByEmail(forgotPasswordRequest.getEmail());
        User user = userMapper.userResponseToUser(userResponse);
        try {
            String token = UUID.randomUUID().toString();
            verifyTokenService.save(user,token);
            //send email
            emailService.sendForgotPasswordMail(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        saveUser(user);
    }
}
