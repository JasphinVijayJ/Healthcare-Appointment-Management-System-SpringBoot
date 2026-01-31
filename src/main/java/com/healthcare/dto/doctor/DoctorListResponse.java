package com.healthcare.dto.doctor;

public class DoctorListResponse {

    private final Long id;
    private final String name;
    private final String specialty;
    private final String qualifications;
    private final int experience;
    private final String imageUrl;

    public DoctorListResponse(Long id, String name, String specialty, String qualifications, int experience, String imageUrl) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.qualifications = qualifications;
        this.experience = experience;
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

    public String getImageUrl() {
        return imageUrl;
    }
}
