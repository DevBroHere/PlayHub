package com.example.application.data.entity;

import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionID;

    @OneToOne(optional = false)
    @JoinColumn(name = "gameID")
    private Games game;

    @OneToOne
    @JoinColumn(name = "ownerID")
    private Users user;

    @OneToMany(mappedBy = "session")
    private List<SessionUsers> sessionUsers;

    private String sessionName;

    private String sessionType;

    private String sessionStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionDate;

    // Default constructor
    public Sessions(){

    }
    public Sessions(Games game, Users owner, String sessionName, String sessionType) {
        this.game = game;
        this.user = owner;
        this.sessionName = sessionName;
        this.sessionType = sessionType;
        this.sessionStatus = "PENDING";
        this.sessionDate = new Timestamp(System.currentTimeMillis());
    }

    public Long getSessionID() { return sessionID; }
    public String getSessionName() { return sessionName; }
    public String getSessionType() { return sessionType; }
    public String getSessionStatus() { return sessionStatus; }
    public Date getSessionDate() { return sessionDate; }

    public void setSessionID(Long sessionID) { this.sessionID = sessionID; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    public void setSessionDate(Date sessionDate) {this.sessionDate = sessionDate; }

    public void setGame(Games game) {
        this.game = game;
    }

    public Games getGame() {
        return game;
    }

    public Users getUser() {
        if (user == null) {
            user = new Users();
        }
        return user;
    }


}
