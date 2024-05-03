package com.example.hrm.service.impl;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.mapper.DepartmentMapper;
import com.example.hrm.repository.DepartmentRepository;
import com.example.hrm.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
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
    public List<DepartmentResponse> getAllDepartment() {
        List<Department> list = departmentRepository.findAll();
        List<DepartmentResponse> listResponse = new ArrayList<>();
        for(Department d: list) {
            listResponse.add(departmentMapper.toDepartmentResponse(d));
        }
        return listResponse;
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
        DepartmentResponse response = new DepartmentResponse(department.getId(),department.getName(),department.getQuantity());
        return response;
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public boolean departmentExists(DepartmentRequest departmentRequest) {
        return departmentRepository.existsByName(departmentRequest.getName());
    }

    @Override
    public List<DepartmentResponse> findByName(String name) {
        List<Department> list = departmentRepository.findByName(name);
        List<DepartmentResponse> listResponse = new ArrayList<>();
        for(Department d: list) {
            listResponse.add(departmentMapper.toDepartmentResponse(d));
        }
        return listResponse;
    }
}
