package com.example.application.data.repository;

import com.example.application.data.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Games, Long> {
    List<Games> findByGameTitle(String gameTitle);
}
