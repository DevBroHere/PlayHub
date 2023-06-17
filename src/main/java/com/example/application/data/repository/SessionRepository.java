package com.example.application.data.repository;

import com.example.application.data.entity.Games;
import com.example.application.data.entity.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Sessions, Long> {
    List<Sessions> findBySessionType(String sessionType);

    @Query("SELECT s FROM Sessions s WHERE s.sessionStart > ?1 ORDER BY s.sessionStart ASC")
    List<Sessions> findUpcomingSessions(LocalDateTime currentDate);

    @Query("SELECT s FROM Sessions s WHERE s.sessionType = :sessionType AND s.sessionStatus = :sessionStatus")
    List<Sessions> findBySessionTypeAndSessionStatus(@Param("sessionType") String sessionType, @Param("sessionStatus") String sessionStatus);
    @Query("SELECT s FROM Sessions s WHERE s.sessionID IN :sessionIds")
    List<Sessions> findSessionsBySessionIds(@Param("sessionIds") List<Long> sessionIds);

    @Query("SELECT s FROM Sessions s WHERE s.sessionStatus = 'OPEN' AND s.sessionStart < local datetime ")
    List<Sessions> findSessionsBySessionStatusAndSessionStartIsSmallerThanCurrentDate();

    @Query("SELECT distinct s.game.gameTitle FROM Sessions s WHERE s.sessionID IN :sessionIds order by s.game.gameTitle")
    List<String> findUniqueGamesBySessionidsOrderedByGame(@Param("sessionIds") List<Long> sessionsIds);

    @Query("SELECT COUNT(*) FROM Sessions s WHERE s.sessionID IN :sessionIds " +
            "GROUP BY s.game.gameTitle ORDER BY s.game.gameTitle")
    List<Integer> findSessionCountBySessionIdsOrderedByGame(@Param("sessionIds") List<Long> sessionsIds);
}
