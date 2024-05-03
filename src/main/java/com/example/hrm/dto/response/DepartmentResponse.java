package com.example.hrm.dto.response;

public class DepartmentResponse {
    private int id;
    private String name;
    private int quantity;

    public DepartmentResponse(int id,String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
