package com.healthcare.dto.appointment;

import com.healthcare.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class LatestAppointmentResponse {

    private final Long appointmentId;
    private final String patientName;
    private final int patientAge;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final AppointmentStatus status;

    public LatestAppointmentResponse(Long appointmentId, String patientName, int patientAge, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters
    public Long getAppointmentId() {
        return appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
}
