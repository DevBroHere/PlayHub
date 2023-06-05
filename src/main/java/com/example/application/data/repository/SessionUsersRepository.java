package com.example.application.data.repository;

import com.example.application.data.entity.SessionUsers;
import com.example.application.data.entity.Sessions;
import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionUsersRepository extends JpaRepository<SessionUsers, Long> {
    SessionUsers findBySessionAndUser(Sessions session, Users user);
}
