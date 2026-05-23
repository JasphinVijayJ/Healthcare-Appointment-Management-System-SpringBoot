package com.healthcare.controller;

import com.healthcare.dto.appointment.AppointmentRequest;
import com.healthcare.dto.appointment.PatientAppointmentResponse;
import com.healthcare.dto.common.ApiResponse;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/book")
    public ResponseEntity<ApiResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request, @AuthenticationPrincipal Long loggedInUserId) {

        appointmentService.bookAppointment(loggedInUserId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(SuccessMessage.APPOINTMENT_BOOKED_SUCCESS.getMessage()));
    }


    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my-appointments")
    public ResponseEntity<List<PatientAppointmentResponse>> getMyAppointments(@AuthenticationPrincipal Long loggedInUserId) {

        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(loggedInUserId));
    }


    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse> cancelAppointment(@AuthenticationPrincipal Long loggedInUserId, @PathVariable Long appointmentId) {

        appointmentService.cancelAppointment(appointmentId, loggedInUserId);

        return ResponseEntity.ok(new ApiResponse(SuccessMessage.APPOINTMENT_CANCELLED_SUCCESS.getMessage()));
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/update-status")
    public ResponseEntity<ApiResponse> updateAppointmentStatus(@RequestParam Long appointmentId, @RequestParam String status) {

        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, status));
    }
}
