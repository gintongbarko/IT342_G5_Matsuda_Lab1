package com.it342.timesheets.repository;

import com.it342.timesheets.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {

    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);

    Optional<UserSession> findBySessionToken(String sessionToken);

    List<UserSession> findByUser_UserIdAndIsActiveTrue(Integer userId);
}
