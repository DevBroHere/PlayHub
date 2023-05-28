package com.example.application.data.entity;

import jakarta.persistence.*;

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


}
