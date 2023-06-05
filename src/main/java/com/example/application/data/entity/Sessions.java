
package com.example.application.data.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
public class Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionID;

    @ManyToOne(optional = false)
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

    private LocalDateTime sessionStart;

    private String sessionPassword;

    public Sessions(Games game, Users owner, String sessionName, String sessionType, LocalDateTime sessionStart, String sessionPassword) {
        this.game = game;
        this.user = owner;
        this.sessionName = sessionName;
        this.sessionType = sessionType;
        this.sessionStatus = "OPEN";
        this.sessionDate = new Timestamp(System.currentTimeMillis());
        this.sessionStart = sessionStart;
        this.sessionPassword = sessionPassword;
    }

    public Sessions() {

    }

    public Long getSessionID() { return sessionID; }
    public String getSessionName() { return sessionName; }
    public String getSessionType() { return sessionType; }
    public String getSessionStatus() { return sessionStatus; }
    public Date getSessionDate() { return sessionDate; }
    public LocalDateTime getSessionStart() { return sessionStart; }
    public String getSessionPassword() { return sessionPassword; }

    public void setSessionID(Long sessionID) { this.sessionID = sessionID; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    public void setSessionStart(LocalDate sessionStart) { this.sessionStart = sessionStart.atStartOfDay(); }
    public void setSessionPassword(String sessionPassword) { this.sessionPassword = sessionPassword; }

    public void setUser(Users user) {
        this.user = user;
    }

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

    public List<SessionUsers> getSessionUsers() {
        return sessionUsers;
    }

    public void setSessionUsers(List<SessionUsers> sessionUsers) {
        this.sessionUsers = sessionUsers;
    }

    public String getPassword() {
        return sessionPassword;
    }
}