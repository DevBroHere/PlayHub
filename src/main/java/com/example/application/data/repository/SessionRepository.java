package com.example.application.data.repository;

import com.example.application.data.entity.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Sessions, Long> {
    List<Sessions> findBySessionType(String sessionType);
}
