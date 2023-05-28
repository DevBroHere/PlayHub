package com.example.application.data.repository;

import com.example.application.data.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Logs, Long> {
}
