package com.example.application.data.repository;

import com.example.application.data.entity.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Sessions, Long> {
    List<Sessions> findBySessionType(String sessionType);

    @Query("SELECT s FROM Sessions s WHERE s.sessionStart > ?1 ORDER BY s.sessionStart ASC")
    List<Sessions> findUpcomingSessions(LocalDateTime currentDate);
}
