package com.example.application.data.entity;

import jakarta.persistence.*;

@Entity
public class Friendships {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long friendshipID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Users friend;

    private String friendshipStatus;

    public Long getFriendshipID() { return friendshipID; }
    public String getFriendshipStatus() { return friendshipStatus; }

    public void setFriendshipID(Long friendshipID) { this.friendshipID = friendshipID; }
    public void setFriendshipStatus(String friendshipStatus) { this.friendshipStatus = friendshipStatus; }
}
