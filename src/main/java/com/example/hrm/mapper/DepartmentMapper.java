package com.example.hrm.mapper;

import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentResponse toDepartmentResponse(Department department);
}
