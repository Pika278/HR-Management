package com.example.hrm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    @NotBlank(message = "Không được bỏ trống")
    private String name;
    private int quantity;

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
