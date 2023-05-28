package com.example.application.data.repository;

import com.example.application.data.entity.Friendships;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendships, Long> {
}
