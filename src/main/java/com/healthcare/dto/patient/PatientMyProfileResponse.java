package com.healthcare.dto.patient;

import com.healthcare.enums.Gender;
import com.healthcare.enums.Role;

import java.time.LocalDate;
import java.util.List;

public class PatientMyProfileResponse {

    private final String name;
    private final String email;
    private final Role role;
    private final String phone;
    private final LocalDate dob;
    private final String address;
    private final String bloodGroup;
    private final List<String> allBloodGroup;
    private final Gender gender;
    private final List<String> allGender;
    private final String imageUrl;
    private final String message;

    public PatientMyProfileResponse(String name, String email, Role role, String phone, LocalDate dob, String address, String bloodGroup, List<String> allBloodGroup, Gender gender, List<String> allGender, String imageUrl, String message) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.allBloodGroup = allBloodGroup;
        this.gender = gender;
        this.allGender = allGender;
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

    public LocalDate getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public List<String> getAllBloodGroup() {
        return allBloodGroup;
    }

    public Gender getGender() {
        return gender;
    }

    public List<String> getAllGender() {
        return allGender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }
}
