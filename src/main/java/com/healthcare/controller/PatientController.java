package com.healthcare.controller;

import com.healthcare.dto.patient.PatientMyProfileResponse;
import com.healthcare.model.Patient;
import com.healthcare.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<PatientMyProfileResponse> getPatientProfile(@AuthenticationPrincipal Long loggedInUserId) {

        return ResponseEntity.ok(patientService.getPatientProfile(loggedInUserId));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<PatientMyProfileResponse> updatePatientProfile(@AuthenticationPrincipal Long loggedInUserId, @RequestBody Patient request) {

        return ResponseEntity.ok(patientService.updatePatientProfile(loggedInUserId, request));
    }
}
