package com.example.hrm.repository;

import com.example.hrm.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
}
