package com.healthcare.enums;

public enum SuccessMessage {

    PATIENT_REGISTER_SUCCESS("Patient account created successfully!"),
    DOCTOR_REGISTER_SUCCESS("Doctor account created successfully!"),
    LOGIN_SUCCESS("Login successful!"),
    LOGOUT_SUCCESS("Logged out successfully!"),

    APPOINTMENT_BOOKED_SUCCESS("Appointment booked successfully!"),
    APPOINTMENT_CANCELLED_SUCCESS("Appointment cancelled successfully!"),

    PROFILE_IMAGE_UPLOADED_SUCCESS("Profile image uploaded successfully!"),
    PROFILE_UPDATED_SUCCESS("Profile updated successfully!"),

    CONTACT_MESSAGE_SENT("Message sent successfully");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
