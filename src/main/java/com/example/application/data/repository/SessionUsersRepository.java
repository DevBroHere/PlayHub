package com.example.application.data.repository;

import com.example.application.data.entity.SessionUsers;
import com.example.application.data.entity.Sessions;
import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionUsersRepository extends JpaRepository<SessionUsers, Long> {
    SessionUsers findBySessionAndUser(Sessions session, Users user);

    @Query("SELECT su.session.sessionID FROM SessionUsers su WHERE su.user.userID = :userId")
    List<Long> findSessionIdsByUserId(@Param("userId") Long userId);

    List<SessionUsers> findAllBySession(Sessions session);
}
