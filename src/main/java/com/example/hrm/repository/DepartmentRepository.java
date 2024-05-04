package com.example.hrm.repository;

import com.example.hrm.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    @Query(value = "Select * from department d where d.name like %:keyword%", nativeQuery = true)
    List<Department> findByName(String keyword);
    @Query(value = "Select * from department d where d.name like %:keyword%", nativeQuery = true)
    Page<Department> findByNamePaging(Pageable pageable, String keyword);

}
