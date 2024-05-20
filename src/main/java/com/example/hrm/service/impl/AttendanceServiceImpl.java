package com.example.hrm.service.impl;

import com.example.hrm.configuration.CustomUserDetails;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.Attendance;
import com.example.hrm.entity.User;
import com.example.hrm.mapper.AttendanceMapper;
import com.example.hrm.mapper.UserMapper;
import com.example.hrm.repository.AttendanceRepository;
import com.example.hrm.repository.criteria.AttendanceCriteria;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.service.UserService;
import jakarta.transaction.Transactional;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AttendanceCriteria attendanceCriteria;
    private final AttendanceMapper attendanceMapper;

    @Transactional
    @Override
    public void saveAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
    }

    @Override
    public void checkin() {
        LocalDate localDate = LocalDate.now();
        LocalTime checkinTime = LocalTime.now();
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if(attendanceCriteria.getAttendanceById(localDate,userId) == null) {
            Attendance attendance = new Attendance();
            attendance.setDate(localDate);
            attendance.setCheckinTime(LocalTime.parse(formatter.format(checkinTime)));
            UserResponse userResponse = userService.findById(userId);
            User user = userMapper.userResponseToUser(userResponse);
            attendance.setUser(user);
            saveAttendance(attendance);
        }
        else if(getCheckinTimeById(localDate,userId) == null){
            Attendance attendance = attendanceCriteria.getAttendanceById(localDate,userId);
            attendance.setCheckinTime(LocalTime.parse(formatter.format(checkinTime)));
            saveAttendance(attendance);

        }
    }

    @Override
    public void checkout() {
        LocalDate localDate = LocalDate.now();
        LocalTime checkoutTime = LocalTime.now();
        CustomUserDetails myUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if(attendanceCriteria.getAttendanceById(localDate,userId) == null) {
            Attendance attendance = new Attendance();
            attendance.setDate(localDate);
            attendance.setCheckoutTime(LocalTime.parse(formatter.format(checkoutTime)));
            UserResponse userResponse = userService.findById(userId);
            User user = userMapper.userResponseToUser(userResponse);
            attendance.setUser(user);
            saveAttendance(attendance);
        } else {
            Attendance attendance = attendanceCriteria.getAttendanceById(localDate, userId);
            attendance.setCheckoutTime(LocalTime.parse(formatter.format(checkoutTime)));
            saveAttendance(attendance);
        }
    }

    @Override
    public LocalTime getCheckinTimeById(LocalDate localDate, Long userId) {
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate,userId);
        if(attendance == null) return null;
        return attendance.getCheckinTime();
    }

    @Override
    public LocalTime getCheckoutTimeById(LocalDate localDate, Long userId) {
        Attendance attendance = attendanceCriteria.getAttendanceById(localDate,userId);
        if(attendance == null) return null;
        return attendance.getCheckoutTime();
    }

    @Override
    public List<AttendanceResponse> getAttendanceByWeek(LocalDate localDate, Long userId) {
        List<Attendance> list = attendanceCriteria.getAttendanceByWeek(localDate,userId);
        List<AttendanceResponse> responses = new ArrayList<>();
        for(Attendance attendance : list) {
            responses.add(attendanceMapper.toAttendanceResponse(attendance));
        }
        return responses;
    }

    @Override
    public Page<AttendanceResponse> getAttendanceByCurrentMonth(int pageNumber, int pageSize, String sortBy, Long userId) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).descending());
        Page<Attendance> attendanceByCurrentMonth = attendanceCriteria.getAttendanceByCurrentMonth(pageable,userId);
        return attendanceByCurrentMonth.map(attendanceMapper::toAttendanceResponse);
    }

    @Override
    public Page<AttendanceResponse> getAttendanceByMonth(int pageNumber, int pageSize, String sortBy, Long userId, int monthValue, int yearValue) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize, Sort.by(sortBy).descending());
        Page<Attendance> attendanceByCurrentMonth = attendanceCriteria.getAttendanceByMonth(pageable, monthValue, yearValue,userId);
        return attendanceByCurrentMonth.map(attendanceMapper::toAttendanceResponse);
    }
}
