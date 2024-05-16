package com.example.hrm.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String name;
    private Long quantity;

    public DepartmentResponse(Long id,String name, Long quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

}
