package com.healthcare.controller;

import com.healthcare.dto.common.ApiResponse;
import com.healthcare.dto.contact.ContactRequest;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.service.UtilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/utility")
public class UtilityController {

    private final UtilityService utilityService;

    public UtilityController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @PostMapping("/contact-form")
    public ResponseEntity<ApiResponse<Void>> sendMessage(@Valid @RequestBody ContactRequest request) {

        utilityService.sendContactEmail(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(SuccessMessage.CONTACT_MESSAGE_SENT.getMessage()));
    }


    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @PostMapping("/upload-profile-image")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@AuthenticationPrincipal Long loggedInUserId,
                                                          @RequestParam("image") MultipartFile image,
                                                          @RequestParam("role") String role) {

        return ResponseEntity.ok(utilityService.uploadProfileImage(image, loggedInUserId, role));
    }
}
