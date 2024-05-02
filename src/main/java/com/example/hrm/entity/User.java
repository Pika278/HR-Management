package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String citizenId;
    private String gender;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="department_id", referencedColumnName = "id")
    private Department department;
    private Role role;
    private String jobTitle;
    private boolean is_active;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private String deletedBy;
}
