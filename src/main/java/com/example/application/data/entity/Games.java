package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Games {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gameID;
    private String gameTitle;
    private String platformType;

    public Long getGameID() { return gameID; }
    public String getGameTitle() { return gameTitle; }
    public String getPlatformType() { return platformType; }

    public void setGameID(Long gameID) { this.gameID = gameID; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    public void setPlatformType(String platformType) { this.platformType = platformType; }


}
