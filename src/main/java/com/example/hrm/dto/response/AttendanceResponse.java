package com.example.hrm.dto.response;

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
public class AttendanceResponse {
    private Long id;
    private LocalDate date;
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime checkinTime;
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime checkoutTime;
    private Long userId;
}
