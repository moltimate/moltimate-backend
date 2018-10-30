package com.moltimate.moltimatebackend.repository;

import com.moltimate.moltimatebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);
}
