package com.healthcare.controller;

import com.healthcare.dto.common.ApiResponse;
import com.healthcare.dto.contact.ContactRequest;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.service.EmailNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact-form")
public class ContactFormController {

    private final EmailNotificationService emailNotificationService;

    public ContactFormController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> sendMessage(@Valid @RequestBody ContactRequest request) {
        emailNotificationService.sendContactEmail(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(SuccessMessage.CONTACT_MESSAGE_SENT.getMessage()));
    }
}
