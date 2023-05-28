package com.example.application.data.repository;

import com.example.application.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {

    Users getByUserName(String username);

    Users getByActivationCode(String activationCode);
}
