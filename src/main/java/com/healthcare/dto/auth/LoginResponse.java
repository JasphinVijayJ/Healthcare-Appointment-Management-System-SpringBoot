package com.healthcare.dto.auth;

import com.healthcare.enums.Role;

public class LoginResponse {

    private final String email;
    private final Role role;
    private final String message;

    public LoginResponse(String email, Role role, String message) {
        this.email = email;
        this.role = role;
        this.message = message;
    }

    // Getters
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
