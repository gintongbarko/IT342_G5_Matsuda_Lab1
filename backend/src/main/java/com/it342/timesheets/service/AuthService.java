package com.it342.timesheets.service;

import com.it342.timesheets.dto.*;
import com.it342.timesheets.entity.Employee;
import com.it342.timesheets.entity.User;
import com.it342.timesheets.entity.UserRole;
import com.it342.timesheets.entity.UserSession;
import com.it342.timesheets.repository.EmployeeRepository;
import com.it342.timesheets.repository.UserRepository;
import com.it342.timesheets.repository.UserSessionRepository;
import com.it342.timesheets.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final UserSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       EmployeeRepository employeeRepository,
                       UserSessionRepository sessionRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();
        String roleInput = request.getRole() == null ? "" : request.getRole().trim().toUpperCase();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("User already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        UserRole role;
        try {
            role = UserRole.valueOf(roleInput);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        if (role == UserRole.EMPLOYEE) {
            String employerUsername = request.getEmployerUsername() == null ? "" : request.getEmployerUsername().trim();
            if (employerUsername.isBlank()) {
                throw new RuntimeException("Employer is required for employee registration");
            }

            User employer = userRepository.findByUsernameIgnoreCase(employerUsername)
                    .filter(found -> found.getRole() == UserRole.EMPLOYER)
                    .orElseThrow(() -> new RuntimeException("Employer not found"));

            user.setEmployer(employer);
        }

        user = userRepository.save(user);

        if (role == UserRole.EMPLOYEE) {
            Employee employee = new Employee();
            employee.setEmployeeName(user.getUsername());
            employee.setCreatedByUser(user.getEmployer());
            employeeRepository.save(employee);
        }

        String token = jwtUtil.generateToken(user.getUserId());
        saveSession(user.getUserId(), token);

        return new AuthResponse(token, toUserResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        String input = request.getUsername().trim();
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(input, input);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setFailedAttempts((user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setIsActive(false);
                userRepository.save(user);
                throw new RuntimeException("Account locked");
            }
            userRepository.save(user);
            throw new RuntimeException("Invalid credentials");
        }

        user.setFailedAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUserId());
        saveSession(user.getUserId(), token);

        return new AuthResponse(token, toUserResponse(user));
    }

    public void logout(String token) {
        sessionRepository.findBySessionToken(token).ifPresent(session -> {
            session.setIsActive(false);
            sessionRepository.save(session);
        });
    }

    public UserResponse getCurrentUser(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
        Integer userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .map(this::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void saveSession(Integer userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSession> activeSessions = sessionRepository.findByUser_UserIdAndIsActiveTrue(userId);
        activeSessions.forEach(existing -> existing.setIsActive(false));
        if (!activeSessions.isEmpty()) {
            sessionRepository.saveAll(activeSessions);
        }

        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionToken(token);
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        sessionRepository.save(session);
    }

    private UserResponse toUserResponse(User user) {
        String employerName = user.getEmployer() != null ? user.getEmployer().getUsername() : null;
        String role = user.getRole() == null ? UserRole.EMPLOYEE.name() : user.getRole().name();
        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(), role, employerName);
    }

    public java.util.List<UserResponse> searchEmployers(String query) {
        String normalized = query == null ? "" : query.trim();
        return userRepository.findTop10ByRoleAndUsernameContainingIgnoreCaseOrderByUsernameAsc(UserRole.EMPLOYER, normalized)
                .stream()
                .map(this::toUserResponse)
                .toList();
    }
}
