package com.healthcare.controller;

import com.healthcare.dto.doctor.AvailableDayResponse;
import com.healthcare.dto.doctor.DoctorListResponse;
import com.healthcare.dto.doctor.DoctorProfileResponse;
import com.healthcare.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<DoctorListResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<AvailableDayResponse>> getDoctorAvailability(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorAvailabilityForNextWeek(doctorId));
    }
}
