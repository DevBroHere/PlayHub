package com.example.application.data.entity;

import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

@Entity
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userID;
    private String userName;
    private String email;
    private String passwordSalt;
    private String passwordHash;
    private String userNick;
    private String userDescription;
    private String userRole;
    private String activationCode;
    private boolean active;
    @OneToMany(mappedBy = "user")
    private List<SessionUsers> sessionUsers;

    public Users() {

    }

    public Users(String username, String email, String password, String role) {
        this.userName = username;
        this.email = email;
        this.userRole = role;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
    }
    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
    }

    public Long getUserID() { return userID; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getPasswordSalt() { return passwordSalt; }
    public String getPasswordHash() { return passwordHash; }
    public String getUserNick() { return userNick; }
    public String getUserDescription() { return userDescription; }
    public Role getUserRole() { return Role.valueOf(userRole); }
    public String getActivationCode() {
        return activationCode;
    }

    public void setUserID(Long id) { this.userID = userID; } // Potentially to delete because of auto generation
    public void setUserName(String userName) { this.userName = userName; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setUserNick(String userNick) { this.userNick = userNick; }
    public void setUserDescription(String userDescription) { this.userDescription = userDescription; }
    public void setUserRole(Role userRole) { this.userRole = userRole.name(); }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
