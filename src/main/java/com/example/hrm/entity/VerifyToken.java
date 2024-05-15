package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.*;


import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VerifyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    private Timestamp expireDate;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public VerifyToken(String token, User user) {
        this.user = user;
        this.token = token;
    }
}
