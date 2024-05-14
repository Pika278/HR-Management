package com.example.hrm.repository;

import com.example.hrm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("select u from User u where u.department.id = ?1")
    Page<User> listDepartmentUser(Pageable pageable, Long departmentId);
    boolean existsByEmail(String email);
    boolean existsByCitizenId(String citizenId);
    Optional<User> findByEmail(String email);
}
