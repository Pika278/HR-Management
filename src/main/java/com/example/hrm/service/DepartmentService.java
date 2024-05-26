package com.example.hrm.service;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentService {
    void saveDepartment(Department department);

    void createDepartment(DepartmentRequest request);

    List<Department> getAllDepartment();

    void updateDepartment(Long id, DepartmentRequest departmentRequest);

    DepartmentResponse findById(Long id);

    void deleteDepartment(Long id);

    boolean departmentExists(DepartmentRequest departmentRequest);

    Page<DepartmentResponse> findByNamePaging(int pageNumber, int pageSize, String sortBy, String name);

    Page<DepartmentResponse> findByNameActivePaging(int pageNumber, int pageSize, String sortBy, String name);
}
