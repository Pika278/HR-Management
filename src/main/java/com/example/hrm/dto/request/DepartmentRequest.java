package com.example.hrm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    private int id;
    @NotBlank(message = "Không được bỏ trống")
    private String name;
    public DepartmentRequest() {
    }

    public DepartmentRequest(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
