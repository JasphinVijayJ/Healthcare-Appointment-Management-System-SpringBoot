package com.healthcare.dto.auth;

import com.healthcare.enums.Role;

public class LoginResponse {

    private final String message;
    private final Role role;
    private final String token;

    public LoginResponse(String message, Role role, String token) {
        this.message = message;
        this.role = role;
        this.token = token;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
