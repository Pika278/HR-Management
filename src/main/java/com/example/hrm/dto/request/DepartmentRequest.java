package com.example.hrm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    private Long id;
    @NotBlank(message = "Không được bỏ trống")
    private String name;
    public DepartmentRequest() {
    }

    public DepartmentRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
