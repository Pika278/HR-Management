package com.example.hrm.service.impl;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.repository.DepartmentRepository;
import com.example.hrm.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void createDepartment(DepartmentRequest request) {
        if(departmentRepository.existsByName(request.getName())) {
            throw new AppException(ErrorMessage.DEPARTMENT_EXISTED);
        }
        Department department = new Department(request.getName(),0);
        departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Override
    public void updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if(optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            department.setName(departmentRequest.getName());
            departmentRepository.save(department);
        }
        else {
            throw new AppException(ErrorMessage.DEPARTMENT_NOT_FOUND);
        }
    }

    @Override
    public DepartmentResponse findById(Long id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        Department department = optionalDepartment.get();
        DepartmentResponse response = new DepartmentResponse(department.getName());
        return response;
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
