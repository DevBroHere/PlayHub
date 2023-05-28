package com.example.application.data.repository;

import com.example.application.data.entity.SessionUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionUsersRepository extends JpaRepository<SessionUsers, Long> {
}
