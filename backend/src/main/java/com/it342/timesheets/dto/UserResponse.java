package com.it342.timesheets.dto;

public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private String role;
    private String employerName;

    public UserResponse() {}

    public UserResponse(Integer userId, String username, String email, String role, String employerName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.employerName = employerName;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
}
