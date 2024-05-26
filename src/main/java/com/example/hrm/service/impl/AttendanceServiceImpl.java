package com.example.hrm.service.impl;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.request.AddAttendanceRequest;
import com.example.hrm.dto.request.UpdateAttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Attendance;
import com.example.hrm.entity.User;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.mapper.AttendanceMapper;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.repository.AttendanceRepository;
import com.example.hrm.repository.UserRepository;
import com.example.hrm.repository.criteria.AttendanceCriteria;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AttendanceCriteria attendanceCriteria;
    private final AttendanceMapper attendanceMapper;
    private final UserRepository userRepository;

    @Override
    public void checkin() {
        LocalDate localDate = LocalDate.now();
        LocalTime checkinTime = LocalTime.now();
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Attendance attendance = new Attendance();
        attendance.setDate(localDate);
        attendance.setCheckinTime(LocalTime.parse(formatter.format(checkinTime)));
        UserResponse userResponse = userService.findById(userId);
        User user = userMapper.userResponseToUser(userResponse);
        attendance.setUser(user);
        attendanceRepository.save(attendance);
    }

    @Override
    public void checkout() {
        LocalDate localDate = LocalDate.now();
        LocalTime checkoutTime = LocalTime.now();
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate, userId);
        attendance.setCheckoutTime(LocalTime.parse(formatter.format(checkoutTime)));
        attendanceRepository.save(attendance);
    }

    @Override
    public LocalTime getCheckinTimeById(LocalDate localDate, Long userId) {
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate, userId);
        if (attendance == null) return null;
        return attendance.getCheckinTime();
    }

    @Override
    public LocalTime getCheckoutTimeById(LocalDate localDate, Long userId) {
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate, userId);
        if (attendance == null) return null;
        return attendance.getCheckoutTime();
    }

    @Override
    public List<AttendanceResponse> getAttendanceByWeek(LocalDate localDate, Long userId) {
        List<Attendance> list = attendanceCriteria.getAttendanceByWeek(localDate, userId);
        List<AttendanceResponse> responses = new ArrayList<>();
        for (Attendance attendance : list) {
            responses.add(attendanceMapper.toAttendanceResponse(attendance));
        }
        return responses;
    }

    @Override
    public Page<AttendanceResponse> getAttendanceByCurrentMonth(int pageNumber, int pageSize, String sortBy, Long userId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).descending());
        Page<Attendance> attendanceByCurrentMonth = attendanceCriteria.getAttendanceByCurrentMonth(pageable, userId);
        return attendanceByCurrentMonth.map(attendanceMapper::toAttendanceResponse);
    }

    @Override
    public Page<AttendanceResponse> getAttendanceByMonth(int pageNumber, int pageSize, String sortBy, Long userId, int monthValue, int yearValue) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).descending());
        Page<Attendance> attendanceByCurrentMonth = attendanceCriteria.getAttendanceByMonth(pageable, monthValue, yearValue, userId);
        return attendanceByCurrentMonth.map(attendanceMapper::toAttendanceResponse);
    }

    @Override
    public AttendanceResponse findAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        if(attendance != null) {
            AttendanceResponse attendanceResponse = attendanceMapper.toAttendanceResponse(attendance);
            attendanceResponse.setUserId(attendance.getUser().getId());
            return attendanceResponse;
        }
        else {
            throw new AppException(ErrorMessage.ATTENDANCE_NOT_FOUND);
        }
    }

    @Override
    public void updateAttendance(Long id, UpdateAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        if (attendance != null) {
            attendanceMapper.updateAttendance(attendance, request);
            attendanceRepository.save(attendance);
        } else {
            throw new AppException(ErrorMessage.ATTENDANCE_NOT_FOUND);
        }
    }

    @Override
    public void addAttendance(Long userId, AddAttendanceRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        Attendance attendance = attendanceCriteria.getAttendanceById(request.getDate(), userId);
        if (attendance == null) {
            if (user != null) {
                Attendance newAttendance = attendanceMapper.toAttendance(request);
                newAttendance.setUser(user);
                attendanceRepository.save(newAttendance);
            }
        } else {
            throw new AppException(ErrorMessage.ATTENDANCE_EXISTED);
        }
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.findById(id).ifPresent(attendanceRepository::delete);
    }

    @Override
    public AttendanceResponse getAttendanceByDateUser(LocalDate localDate, Long userId) {
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate, userId);
        if (attendance != null) {
            AttendanceResponse attendanceResponse = attendanceMapper.toAttendanceResponse(attendance);
            attendanceResponse.setUserId(attendance.getUser().getId());
            return attendanceResponse;
        }
        return null;
    }

}
