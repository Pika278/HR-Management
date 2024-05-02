package com.example.hrm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    DEPARTMENT_EXISTED(1001,"Department existed"),
    DEPARTMENT_NOT_FOUND(1002,"Department not found");
    private int errorCode;
    private String message;
}
