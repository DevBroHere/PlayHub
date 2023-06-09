package com.example.application.data.repository;

import com.example.application.data.entity.Logs;
import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Logs, Long> {

    List<Logs> findAllByUser(Users user);

}
