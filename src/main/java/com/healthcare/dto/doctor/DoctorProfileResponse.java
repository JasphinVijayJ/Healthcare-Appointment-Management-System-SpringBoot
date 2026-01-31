package com.healthcare.dto.doctor;

public class DoctorProfileResponse {

    private final Long id;
    private final String name;
    private final String specialty;
    private final String qualifications;
    private final int experience;
    private final double consultationFee;
    private final String languages;
    private final String imageUrl;

    public DoctorProfileResponse(Long id, String name, String specialty, String qualifications, int experience, double consultationFee, String languages, String imageUrl) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.qualifications = qualifications;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.languages = languages;
        this.imageUrl = imageUrl;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
