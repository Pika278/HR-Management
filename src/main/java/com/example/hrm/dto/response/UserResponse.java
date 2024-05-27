package com.example.hrm.dto.response;

import com.example.hrm.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String citizenId;
    private String gender;
    private String fullName;
    private String dateOfBirth;
    private String phoneNumber;
    private Long departmentId;
    private Role role;
    private String jobTitle;
    private boolean is_active;
    private Date createdAt;
    private Date updatedAt;

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
