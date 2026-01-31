package com.healthcare.dto.appointment;

import com.healthcare.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class PatientAppointmentResponse {

    private final Long appointmentId;
    private final Long patientId;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final AppointmentStatus status;

    private final Long doctorId;
    private final String doctorName;
    private final String doctorSpecialty;
    private final double doctorFee;
    private final String doctorImage;

    public PatientAppointmentResponse(Long appointmentId, Long patientId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status, Long doctorId, String doctorName, String doctorSpecialty, double doctorFee, String doctorImage) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.doctorFee = doctorFee;
        this.doctorImage = doctorImage;
    }

    // Getters
    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getPatientId() {
        return patientId;
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

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public double getDoctorFee() {
        return doctorFee;
    }

    public String getDoctorImage() {
        return doctorImage;
    }
}
