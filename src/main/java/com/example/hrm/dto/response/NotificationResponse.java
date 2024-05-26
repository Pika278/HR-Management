package com.example.hrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String publishedTime;
    private boolean isPublished;


    public boolean isPublished() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime parsedDateTime;
        try {
            parsedDateTime = LocalDateTime.parse(publishedTime, formatter);
            return parsedDateTime.isBefore(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date-time format: " + e.getMessage());
        }
        return false;
    }
}
