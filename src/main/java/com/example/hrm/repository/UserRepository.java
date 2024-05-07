package com.example.hrm.repository;

import com.example.hrm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query(nativeQuery = true, value = "SELECT * from user u WHERE u.email LIKE %:keyword% " +
            "OR u.id LIKE %:keyword% " +
            "OR u.full_name LIKE %:keyword% " +
            "OR u.citizen_id LIKE %:keyword% " +
            "OR u.job_title LIKE %:keyword% " +
            "OR u.department_id LIKE %:keyword% " +
            "OR u.role LIKE %:keyword%")
    Page<User> findUser(Pageable pageable, String keyword);
    boolean existsByEmail(String email);
    boolean existsByCitizenId(String citizenId);
}
