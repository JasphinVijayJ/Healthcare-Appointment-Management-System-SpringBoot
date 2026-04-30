package com.healthcare.dto.auth;

import com.healthcare.enums.Role;

public class LoginResponse {

    private final Long id;
    private final String email;
    private final Role role;
    private final String message;

    public LoginResponse(Long id, String email, Role role, String message) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
