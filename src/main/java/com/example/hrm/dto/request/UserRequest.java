package com.example.hrm.dto.request;

import com.example.hrm.entity.Department;
import com.example.hrm.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class UserRequest {
    private Long id;
    @Email
    @NotBlank(message = "Không được bỏ trống")
    private String email;
    @NotBlank(message = "Không được bỏ trống")
    private String citizenId;
    private String gender;
    @NotBlank(message = "Không được bỏ trống")
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Long departmentId;
    private Role role;
    private String jobTitle;
    private boolean is_active;

    public UserRequest() {
    }

    public UserRequest(Long id, String email, String citizenId, String gender, String fullName, LocalDate dateOfBirth, String phoneNumber, Long departmentId, Role role, String jobTitle, boolean is_active) {
        this.id = id;
        this.email = email;
        this.citizenId = citizenId;
        this.gender = gender;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.departmentId = departmentId;
        this.role = role;
        this.jobTitle = jobTitle;
        this.is_active = is_active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public String getGender() {
        return gender;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Role getRole() {
        return role;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}
