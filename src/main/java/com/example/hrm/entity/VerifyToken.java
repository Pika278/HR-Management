package com.example.hrm.entity;

import jakarta.persistence.*;


import java.sql.Timestamp;

@Entity
public class VerifyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    private Timestamp expireDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public VerifyToken() {
    }

    public VerifyToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public int getId() {
        return id;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
