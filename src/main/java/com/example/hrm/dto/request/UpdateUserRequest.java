package com.example.hrm.dto.request;

import com.example.hrm.entity.Department;
import com.example.hrm.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private Long id;
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

}
