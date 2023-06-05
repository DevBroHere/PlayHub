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

    public Long getSessionUserID() {
        return sessionUserID;
    }

    public void setSessionUserID(Long sessionUserID) {
        this.sessionUserID = sessionUserID;
    }

    public Sessions getSession() {
        return session;
    }

    public void setSession(Sessions session) {
        this.session = session;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
