package com.example.application.data.repository;

import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Integer> {

    Users getByUserName(String username);

    Users getByActivationCode(String activationCode);

    @Query("SELECT u FROM Users u " +
            "WHERE u.active = true " +
            "AND u.userID != :userId " +
            "AND u.userID NOT IN " +
            "(SELECT f.friend FROM Friendships f WHERE f.user.userID = :userId)")
    List<Users> findNonFriendActiveUsers(@Param("userId") Long userId);

    @Query("SELECT u FROM Users u " +
    "WHERE u.active = true " +
    "AND u.userID != :userId " +
    "AND u.userID IN (SELECT f.friend FROM Friendships f WHERE f.user.userID = :userId)")
    List<Users> findFriendActiveUsers(@Param("userId") Long userId);
}
