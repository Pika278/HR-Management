package com.example.hrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int quantity;
    @OneToMany(mappedBy = "department")
    private List<User> users;

    public Department(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
