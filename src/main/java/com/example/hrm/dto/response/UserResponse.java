package com.example.hrm.dto.response;

import com.example.hrm.entity.Department;
import com.example.hrm.entity.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

public class UserResponse {
    private int id;
    private String email;
    private String citizenId;
    private String gender;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Department department;
    private Role role;
    private String jobTitle;
    private boolean is_active;
    private Date createdAt;
    private Date updatedAt;

    public UserResponse() {
    }

    public UserResponse(int id, String email, String citizenId, String gender, String fullName, LocalDate dateOfBirth, String phoneNumber, Department department, Role role, String jobTitle, boolean is_active, Date createdAt, Date updatedAt) {
        this.id = id;
        this.email = email;
        this.citizenId = citizenId;
        this.gender = gender;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.role = role;
        this.jobTitle = jobTitle;
        this.is_active = is_active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
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

    public Department getDepartment() {
        return department;
    }

    public Role getRole() {
        return role;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
