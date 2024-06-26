package com.example.hrm.service.impl;

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
import com.example.hrm.repository.DepartmentRepository;
import com.example.hrm.repository.UserRepository;
import com.example.hrm.repository.criteria.UserCriteria;
import com.example.hrm.service.DepartmentService;
import com.example.hrm.service.EmailService;
import com.example.hrm.service.UserService;
import com.example.hrm.service.VerifyTokenService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final DepartmentRepository departmentRepository;
    private final SessionRegistry sessionRegistry;

    @Transactional
    @Override
    public User createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorMessage.USER_EXISTED);
        }
        User user = userMapper.userRequestToUser(userRequest);
        user.setIs_active(false);
        Optional<Department> department = departmentRepository.findById(userRequest.getDepartmentId());
        if (department.isPresent()) {
            user.setDepartment(department.get());
            userRepository.save(user);
            String token = UUID.randomUUID().toString();
            verifyTokenService.save(user, token);
            Long quantity = department.get().getQuantity() + 1;
            department.get().setQuantity(quantity);
            departmentService.saveDepartment(department.get());
            return user;
        }
        else {
            throw new AppException(ErrorMessage.DEPARTMENT_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public void updateUser(Long id, UpdateUserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userMapper.updateUser(user, userRequest);
            Department department = departmentRepository.findById(user.getDepartment().getId()).orElse(null);
            Department departmentRequest = departmentRepository.findById(userRequest.getDepartmentId()).orElse(null);
            user.setDepartment(departmentRequest);
            userRepository.save(user);
            //update department quantity
            if (department != null) {
                department.setQuantity(department.getQuantity() - 1);
                departmentService.saveDepartment(department);
            }
            if (departmentRequest != null) {
                departmentRequest.setQuantity(departmentRequest.getQuantity() + 1);
                departmentService.saveDepartment(departmentRequest);
            }
            List<Object> principals = sessionRegistry.getAllPrincipals();
            for (Object principal : principals) {
                if (principal instanceof UserDetails userDetails) {
                    if (userDetails.getUsername().equals(user.getEmail())) {
                        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
                        for (SessionInformation session : sessions) {
                            session.expireNow(); // Invalidate the session
                        }
                    }
                }
            }
        } else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            VerifyToken verifyToken = verifyTokenService.findByUser(user);
            if(verifyToken != null) {
                verifyTokenService.deleteToken(verifyToken);

            }
            userRepository.deleteById(id);
            DepartmentResponse departmentResponse = departmentService.findById(user.getDepartment().getId());
            Department department = departmentMapper.departmentResponsetoDepartment(departmentResponse);
            department.setQuantity(department.getQuantity() - 1);
            departmentService.saveDepartment(department);
        } else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }

    }

    @Override
    public UserResponse findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        UserResponse userResponse = new UserResponse();
        if (user.isPresent()) {
            userResponse = userMapper.toUserResponse(user.get());
            userResponse.setDepartmentId(user.get().getDepartment().getId());
            return userResponse;
        } else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
    }


    @Override
    public Page<UserResponse> listUserfindByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<User> listUserFindByKeyword = userCriteria.findByKeyword(pageable, keyword);

        return listUserFindByKeyword.map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> listUserActiveFindByKeywordPaging(int pageNumber, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<User> listUserActiveFindByKeywordPaging = userCriteria.findUserActiveByKeyword(pageable, keyword);
        return listUserActiveFindByKeywordPaging.map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> listDepartmentUserPaging(int pageNumber, int pageSize, String sortBy, Long departmentId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<User> listDepartmentUserPaging = userRepository.listDepartmentUser(pageable, departmentId);
        return listDepartmentUserPaging.map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> listDepartmentUserActivePaging(int pageNumber, int pageSize, String sortBy, Long departmentId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<User> listDepartmentUserActivePaging = userRepository.listDepartmentUserActive(pageable, departmentId);
        return listDepartmentUserActivePaging.map(userMapper::toUserResponse);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean citizenIdExists(String citizenId) {
        return userRepository.existsByCitizenId(citizenId);

    }

    @Transactional
    @Override
    public void changeActive(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIs_active(!user.isIs_active());
            userRepository.save(user);
            List<Object> principals = sessionRegistry.getAllPrincipals();
            for (Object principal : principals) {
                if (principal instanceof UserDetails userDetails) {
                    if (userDetails.getUsername().equals(user.getEmail())) {
                        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
                        for (SessionInformation session : sessions) {
                            session.expireNow(); // Invalidate the session
                        }
                    }
                }
            }
        } else {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public void createPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setIs_active(true);
        VerifyToken token = verifyTokenService.findByUser(user);
        if(token != null) {
            verifyTokenService.deleteToken(token);
        }
        userRepository.save(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return userMapper.toUserResponse(user);
    }

    @Async
    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElse(null);
        if(user != null) {
            try {
                String token = UUID.randomUUID().toString();
                if(verifyTokenService.findByUser(user) == null) {
                    verifyTokenService.save(user, token);
                }
                else {
                    verifyTokenService.updateToken(user,token);
                }
                emailService.sendForgotPasswordMail(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
