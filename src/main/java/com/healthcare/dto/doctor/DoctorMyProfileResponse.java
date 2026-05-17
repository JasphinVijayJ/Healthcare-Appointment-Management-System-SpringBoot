package com.healthcare.dto.doctor;

import com.healthcare.enums.Role;

public class DoctorMyProfileResponse {

    private final String name;
    private final String email;
    private final Role role;
    private final String phone;
    private final String specialty;
    private final String qualifications;
    private final int experience;
    private final double consultationFee;
    private final String languages;
    private final String imageUrl;
    private final String message;

    public DoctorMyProfileResponse(String name, String email, Role role, String phone, String specialty, String qualifications, int experience, double consultationFee, String languages, String imageUrl, String message) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.specialty = specialty;
        this.qualifications = qualifications;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.languages = languages;
        this.imageUrl = imageUrl;
        this.message = message;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getQualifications() {
        return qualifications;
    }

    public int getExperience() {
        return experience;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public String getLanguages() {
        return languages;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }
}
