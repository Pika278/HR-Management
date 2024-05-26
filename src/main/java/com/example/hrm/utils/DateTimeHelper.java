package com.example.hrm.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {
    public static String formatDate(LocalDate date) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }
    public static String formatTime(LocalTime time) {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(time);
    }
}
