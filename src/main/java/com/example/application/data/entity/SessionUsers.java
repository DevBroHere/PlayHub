package com.example.application.data.entity;

import jakarta.persistence.*;

@Entity
public class SessionUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionUserID;

    @ManyToOne
    @JoinColumn(name = "sessionID")
    private Sessions session;

    @ManyToOne
    @JoinColumn(name = "userID")
    private Users user;
}
