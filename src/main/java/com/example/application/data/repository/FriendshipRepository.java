package com.example.application.data.repository;

import com.example.application.data.entity.Friendships;
import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendships, Long> {

    @Query("SELECT f from Friendships f " +
    "WHERE f.user.userID = :userId")
    List<Friendships> findAllFriendshipsByUserId(@Param("userId") Long userId);


    Friendships getFriendshipsByUserAndFriend(Users userId, Users friendId);
}
