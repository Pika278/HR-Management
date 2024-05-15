package com.example.hrm.exception;

import com.example.hrm.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingException(AppException exception) {
        ErrorMessage errorMessage = exception.getErrorMessage();
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorMessage.getErrorCode());
        apiResponse.setMessage(errorMessage.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
