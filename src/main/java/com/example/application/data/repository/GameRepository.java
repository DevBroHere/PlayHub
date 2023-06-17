package com.example.application.data.repository;

import com.example.application.data.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Games, Long> {
    List<Games> findByGameTitle(String gameTitle);
    @Query("SELECT DISTINCT g.platformType FROM Games g")
    List<String> findDistinctPlatformTypes();

    List<Games> findByGameTitleContainingIgnoreCaseAndPlatformType(String gameTitle, String platformType);

    @Query("SELECT DISTINCT g.platformType FROM Games g WHERE g.gameTitle = :gameTitle")
    List<String> findDistinctPlatformTypesByGameTitle(@Param("gameTitle") String gameTitle);

    @Query("SELECT DISTINCT g.gameTitle FROM Games g")
    List<String> findDistinctGameTitle();

    Games findFirstByGameTitleAndPlatformType(String gameTitle, String platformType);

    @Query("SELECT g FROM Games g WHERE g.gameID IN :gameIds")
    List<Games> findAllByGameIDs(@Param("gameIds") List<Long> gameIds);

    @Query("SELECT DISTINCT g.gameTitle FROM Games g ORDER BY g.gameTitle")
    List<String> findDistinctGameTitleOrderByGameTitle();

}
