package com.example.hrm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
public enum ErrorMessage {
    DEPARTMENT_EXISTED(1001,"Department existed"),
    DEPARTMENT_NOT_FOUND(1002,"Department not found");
    private int errorCode;
    private String message;

    ErrorMessage() {
    }

    ErrorMessage(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
