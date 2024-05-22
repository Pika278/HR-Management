package com.example.hrm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNotificationRequest {
    @NotNull(message = "Không được bỏ trống")
    private String title;
    private String message;
}
