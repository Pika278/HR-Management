package com.example.hrm.dto.request;

import com.example.hrm.entity.Role;
import com.example.hrm.validation.ValidNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private Long id;
    private String email;
    @NotBlank(message = "Không được bỏ trống")
    @ValidNumber
    private String citizenId;
    private String gender;
    @NotBlank(message = "Không được bỏ trống")
    private String fullName;
    @NotBlank(message = "Không được bỏ trống")
    private String dateOfBirth;
    @ValidNumber
    private String phoneNumber;
    private Long departmentId;
    private Role role;
    private String jobTitle;
    private boolean is_active;

}
