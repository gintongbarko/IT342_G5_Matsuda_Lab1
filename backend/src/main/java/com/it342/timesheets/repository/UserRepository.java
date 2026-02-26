package com.it342.timesheets.repository;

import com.it342.timesheets.entity.User;
import com.it342.timesheets.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findTop10ByRoleAndUsernameContainingIgnoreCaseOrderByUsernameAsc(UserRole role, String username);

    List<User> findByRoleAndEmployer_UserIdOrderByUsernameAsc(UserRole role, Integer employerId);
}
