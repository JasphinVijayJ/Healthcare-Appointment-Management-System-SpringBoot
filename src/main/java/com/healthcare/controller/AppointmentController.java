package com.healthcare.controller;

import com.healthcare.dto.appointment.AppointmentRequest;
import com.healthcare.dto.appointment.PatientAppointmentResponse;
import com.healthcare.dto.common.ApiResponse;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(SuccessMessage.APPOINTMENT_BOOKED_SUCCESS.getMessage()));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientAppointmentResponse>> getMyAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId));
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse> cancelAppointment(@PathVariable Long appointmentId, @RequestParam Long patientId) {
        appointmentService.cancelAppointment(appointmentId, patientId);
        return ResponseEntity.ok(new ApiResponse(SuccessMessage.APPOINTMENT_CANCELLED_SUCCESS.getMessage()));
    }
}
