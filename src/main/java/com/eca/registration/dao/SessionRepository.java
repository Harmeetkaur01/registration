package com.eca.registration.dao;

import com.eca.registration.model.User;
import com.eca.registration.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<UserSession,Long> {

    Optional<UserSession> findByUserId(Long id);

}
