package com.healthcare.controller;

import com.healthcare.dto.auth.LoginRequest;
import com.healthcare.dto.auth.LoginResponse;
import com.healthcare.dto.auth.RegisterRequest;
import com.healthcare.dto.common.ApiResponse;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerPatient(@Valid @RequestBody RegisterRequest request) {
        authService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(SuccessMessage.PATIENT_REGISTER_SUCCESS.getMessage()));
    }

    @PostMapping("/admin/register-doctor")
    public ResponseEntity<ApiResponse> registerDoctor(@Valid @RequestBody RegisterRequest request) {
        authService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(SuccessMessage.DOCTOR_REGISTER_SUCCESS.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok(new ApiResponse(SuccessMessage.LOGOUT_SUCCESS.getMessage()));
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkLogout() {
        return ResponseEntity.ok("valid");
    }
}
