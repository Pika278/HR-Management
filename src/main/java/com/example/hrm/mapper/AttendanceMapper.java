package com.example.hrm.mapper;

import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.entity.Attendance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceResponse toAttendanceResponse(Attendance attendance);
}
