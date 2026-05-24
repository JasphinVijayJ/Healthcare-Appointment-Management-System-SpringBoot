package com.healthcare.controller;

import com.healthcare.dto.appointment.LatestAppointmentResponse;
import com.healthcare.dto.common.ApiResponse;
import com.healthcare.dto.doctor.*;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<DoctorListResponse>> getAllDoctors() {

        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // For Patient Only
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDetailsResponse> getDoctorById(@PathVariable Long id) {

        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<AvailableDayResponse>> getDoctorAvailability(@PathVariable Long doctorId) {

        return ResponseEntity.ok(doctorService.getDoctorAvailabilityForNextWeek(doctorId));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/dashboard")
    public ResponseEntity<DoctorDashboardResponse> getDoctorDashboard(@AuthenticationPrincipal Long loggedInUserId) {

        return ResponseEntity.ok(doctorService.getDoctorDashboard(loggedInUserId));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my-profile")
    public ResponseEntity<DoctorMyProfileResponse> getDoctorProfile(@AuthenticationPrincipal Long loggedInUserId) {

        return ResponseEntity.ok(doctorService.getDoctorProfile(loggedInUserId));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/update-profile")
    public ResponseEntity<DoctorMyProfileResponse> updateDoctorProfile(@AuthenticationPrincipal Long loggedInUserId, @RequestBody Doctor request) {

        return ResponseEntity.ok(doctorService.updateDoctorProfile(loggedInUserId, request));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my-schedule")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> getMySchedule(@AuthenticationPrincipal Long loggedInUserId) {

        return ResponseEntity.ok(new ApiResponse<>(
                doctorService.getDoctorSchedule(loggedInUserId),
                SuccessMessage.SCHEDULE_FETCHED_SUCCESS.getMessage()
        ));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/update-my-schedule")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> updateDoctorSchedule(
            @AuthenticationPrincipal Long loggedInUserId, @RequestBody List<DoctorAvailability> requestList) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        doctorService.updateDoctorSchedule(loggedInUserId, requestList),
                        SuccessMessage.SCHEDULE_UPDATED_SUCCESS.getMessage()
                ));
    }

}
