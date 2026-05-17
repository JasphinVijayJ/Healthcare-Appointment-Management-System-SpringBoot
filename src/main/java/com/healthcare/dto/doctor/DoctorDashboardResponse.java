package com.healthcare.dto.doctor;

import com.healthcare.dto.appointment.LatestAppointmentResponse;

import java.util.List;

public class DoctorDashboardResponse {

    private final String doctorName;
    private final double totalEarnings;
    private final long totalAppointments;
    private final long totalPatients;
    private final List<LatestAppointmentResponse> latestAppointments;

    public DoctorDashboardResponse(String doctorName, double totalEarnings, long totalAppointments, long totalPatients, List<LatestAppointmentResponse> latestAppointments) {
        this.doctorName = doctorName;
        this.totalEarnings = totalEarnings;
        this.totalAppointments = totalAppointments;
        this.totalPatients = totalPatients;
        this.latestAppointments = latestAppointments;
    }

    // Getters
    public String getDoctorName() {
        return doctorName;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public List<LatestAppointmentResponse> getLatestAppointments() {
        return latestAppointments;
    }
}
