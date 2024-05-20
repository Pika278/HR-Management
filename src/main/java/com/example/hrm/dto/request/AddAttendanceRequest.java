package com.example.hrm.dto.request;

import com.example.hrm.validation.ValidLocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAttendanceRequest {
    @NotNull(message = "Không được bỏ trống")
    private LocalDate date;
    @ValidLocalTime
    private LocalTime checkinTime;
    @ValidLocalTime
    private LocalTime checkoutTime;
}
