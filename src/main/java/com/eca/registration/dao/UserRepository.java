package com.eca.registration.dao;

import com.eca.registration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String email);

    Optional<User> findByFlatNoAndTowerNo(int flatNo, int towerNo);

}
