package com.example.hrm.dto.request;

import com.example.hrm.entity.Role;
import com.example.hrm.validation.ValidNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private Long id;
    @Email
    @NotBlank(message = "Không được bỏ trống")
    private String email;
    @NotBlank(message = "Không được bỏ trống")
    @ValidNumber
    private String citizenId;
    private String gender;
    @NotBlank(message = "Không được bỏ trống")
    private String fullName;
    @NotBlank(message = "Không được bỏ trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;
    @ValidNumber
    private String phoneNumber;
    private Long departmentId;
    private Role role;
    private String jobTitle;
    private boolean is_active;

    public boolean isIs_active() {
        return is_active;
    }

}
