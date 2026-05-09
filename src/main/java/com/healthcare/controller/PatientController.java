package com.healthcare.controller;

import com.healthcare.dto.patient.PatientDetailsResponse;
import com.healthcare.model.Patient;
import com.healthcare.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailsResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PutMapping("/updateProfile/{id}")
    public ResponseEntity<PatientDetailsResponse> updatePatientProfile(@PathVariable Long id, @RequestBody Patient request) {
        return ResponseEntity.ok(patientService.updatePatientProfile(id, request));
    }
}
