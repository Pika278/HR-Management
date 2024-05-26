package com.example.hrm.service;

import com.example.hrm.dto.request.AddAttendanceRequest;
import com.example.hrm.dto.request.UpdateAttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AttendanceService {
    void checkin();

    void checkout();

    LocalTime getCheckinTimeById(LocalDate localDate, Long userId);

    LocalTime getCheckoutTimeById(LocalDate localDate, Long userId);

    List<AttendanceResponse> getAttendanceByWeek(LocalDate localDate, Long userId);

    Page<AttendanceResponse> getAttendanceByCurrentMonth(int pageNumber, int pageSize, String sortBy, Long userId);

    Page<AttendanceResponse> getAttendanceByMonth(int pageNumber, int pageSize, String sortBy, Long userId, int monthValue, int yearValue);

    AttendanceResponse findAttendanceById(Long id);

    void updateAttendance(Long id, UpdateAttendanceRequest request);

    void addAttendance(Long userId, AddAttendanceRequest request);

    void deleteAttendance(Long id);

    AttendanceResponse getAttendanceByDateUser(LocalDate localDate, Long userId);
}
