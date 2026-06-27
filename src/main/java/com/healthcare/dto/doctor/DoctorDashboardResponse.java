package com.healthcare.dto.doctor;

import com.healthcare.dto.appointment.LatestAppointmentResponse;

import java.util.List;

public class DoctorDashboardResponse {

    private final String doctorName;
    private final String earnings;
    private final String appointments;
    private final String patients;
    private final String completedAppointments;
    private final String appointmentStatus;
    private final List<LatestAppointmentResponse> latestAppointments;

    public DoctorDashboardResponse(String doctorName, String earnings, String appointments, String patients, String completedAppointments, String appointmentStatus, List<LatestAppointmentResponse> latestAppointments) {
        this.doctorName = doctorName;
        this.earnings = earnings;
        this.appointments = appointments;
        this.patients = patients;
        this.completedAppointments = completedAppointments;
        this.appointmentStatus = appointmentStatus;
        this.latestAppointments = latestAppointments;
    }

    // Getters
    public String getDoctorName() {
        return doctorName;
    }

    public String getEarnings() {
        return earnings;
    }

    public String getAppointments() {
        return appointments;
    }

    public String getPatients() {
        return patients;
    }

    public String getCompletedAppointments() {
        return completedAppointments;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public List<LatestAppointmentResponse> getLatestAppointments() {
        return latestAppointments;
    }
}
