package com.example.hrm.mapper;

import com.example.hrm.dto.request.AddAttendanceRequest;
import com.example.hrm.dto.request.UpdateAttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceResponse toAttendanceResponse(Attendance attendance);

    void updateAttendance(@MappingTarget Attendance attendance, UpdateAttendanceRequest request);

    Attendance toAttendance(AddAttendanceRequest attendanceRequest);

    AttendanceResponse attendanceRequestToUpdateAttendanceResponse(UpdateAttendanceRequest updateAttendanceRequest);
    AttendanceResponse attendanceRequestToAddAttendanceResponse(AddAttendanceRequest attendanceRequest);
}
