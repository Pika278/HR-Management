package com.example.hrm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorMessage {
    DEPARTMENT_EXISTED(1001, "Department existed"),
    DEPARTMENT_NOT_FOUND(1002, "Department not found"),
    USER_EXISTED(1003, "Department existed"),
    USER_NOT_FOUND(1004, "User not found"),
    ATTENDANCE_NOT_FOUND(1005, "Attendance not found"),
    ATTENDANCE_EXISTED(1006, "Attendance existed"),
    NOTIFICATION_NOT_FOUND(1007, "Notification not found"),
    DEPARTMENT_HAVE_USERS(1008, "Department have users");
    private int errorCode;
    private String message;

}
