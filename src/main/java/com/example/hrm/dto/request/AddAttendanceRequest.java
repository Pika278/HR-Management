package com.example.hrm.dto.request;

import com.example.hrm.validation.ValidLocalTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAttendanceRequest {
    @NotNull(message = "Không được bỏ trống")
    private LocalDate date;
    @DateTimeFormat(pattern = "HH:mm:ss")
    @ValidLocalTime
    private LocalTime checkinTime;
    @DateTimeFormat(pattern = "HH:mm:ss")
    @ValidLocalTime
    private LocalTime checkoutTime;
}
