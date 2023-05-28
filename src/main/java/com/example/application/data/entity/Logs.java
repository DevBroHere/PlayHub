package com.example.application.data.entity;

import jakarta.persistence.*;
import org.apache.catalina.User;

import java.util.Date;

@Entity
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private Users user;

    private String action;
    private String actionStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;


    public Long getLogID() { return logID; }
    public String getAction() { return action; }
    public String getActionStatus() { return actionStatus; }
    public Date getActionDate() { return actionDate; }

    public void setLogID(Long logID) { this.logID = logID; }
    public void setUser(Users user) { this.user = user; }
    public void setAction(String action) { this.action = action; }
    public void setActionStatus(String actionStatus) { this.actionStatus = actionStatus; }
    public void setActionDate(Date actionDate) { this.actionDate = actionDate; }
}
