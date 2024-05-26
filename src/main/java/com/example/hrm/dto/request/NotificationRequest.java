package com.example.hrm.dto.request;

import com.example.hrm.validation.ValidLocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "Không được bỏ trống")
    private String title;
    @NotBlank(message = "Không được bỏ trống")
    private String message;
    @NotNull(message = "Không được bỏ trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ValidLocalDateTime
    private LocalDateTime publishedTime;
}
