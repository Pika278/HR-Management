package com.example.hrm.dto.request;

import com.example.hrm.validation.ValidLocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendanceRequest {
    @ValidLocalTime
    private LocalTime checkinTime;
    @ValidLocalTime
    private LocalTime checkoutTime;
}
