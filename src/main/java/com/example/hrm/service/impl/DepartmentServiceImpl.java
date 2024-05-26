package com.example.hrm.service.impl;

import com.example.hrm.dto.request.DepartmentRequest;
import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorMessage;
import com.example.hrm.mapper.DepartmentMapper;
import com.example.hrm.repository.DepartmentRepository;
import com.example.hrm.repository.criteria.DepartmentCriteria;
import com.example.hrm.service.DepartmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentCriteria departmentCriteria;

    @Transactional
    @Override
    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public void createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new AppException(ErrorMessage.DEPARTMENT_EXISTED);
        }
        Department department = new Department(request.getName(), 0L);
        departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Override
    public void updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            department.setName(departmentRequest.getName());
            departmentRepository.save(department);
        } else {
            throw new AppException(ErrorMessage.DEPARTMENT_NOT_FOUND);
        }
    }

    @Override
    public DepartmentResponse findById(Long id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            return new DepartmentResponse(department.getId(), department.getName(), department.getQuantity());
        } else {
            throw new AppException(ErrorMessage.DEPARTMENT_NOT_FOUND);
        }
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
    public Page<DepartmentResponse> findByNamePaging(int pageNumber, int pageSize, String sortBy, String name) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        Page<Department> list = departmentRepository.findByNamePaging(pageable, name);

        return list.map(departmentMapper::departmentToDepartmentResponse);
    }

    @Override
    public Page<DepartmentResponse> findByNameActivePaging(int pageNumber, int pageSize, String sortBy, String name) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy).ascending());
        return departmentCriteria.getDepartmentsWithActiveUsers(pageable, name);
    }
}
