package com.healthcare.enums;

public enum ErrorMessage {

    PASSWORD_MISMATCH("Password and Confirm Password do not match."),
    EMAIL_ALREADY_EXISTS("Email is already registered: "),
    INVALID_CREDENTIALS("Invalid email or password."),

    DOCTOR_NOT_FOUND("No doctor found with ID: "),
    PATIENT_NOT_FOUND("No patient found with ID: "),
    SCHEDULE_NOT_FOUND("Schedule not found: "),

    DOCTOR_ALREADY_BOOKED("The doctor is already booked for this time slot."),
    PATIENT_ALREADY_BOOKED("You already have an appointment at this time."),

    PAST_TIME("Cannot book an appointment in the past time for today."),
    DOCTOR_NOT_AVAILABLE("Doctor is not available on the selected date, Day: "),
    INVALID_TIME_SLOT("Selected time slot is invalid or unavailable."),
    DAILY_APPOINTMENT_LIMIT("Patient cannot book more than 2 appointments per day."),
    APPOINTMENT_NOT_FOUND("No appointment found with ID: "),

    UNAUTHORIZED_APPOINTMENT_CANCEL("You are not authorized to cancel this appointment."),
    INVALID_APPOINTMENT_CANCELLATION("Only booked appointments can be cancelled !!"),
    CANNOT_CANCEL_PAST("Cannot cancel appointments scheduled before today."),
    CANNOT_CANCEL_PAST_TODAY("Cannot cancel appointments with a past time today."),

    UNSUPPORTED_APPOINTMENT_STATUS("Invalid appointment status !!"),
    INVALID_APPOINTMENT_STATUS_CHANGE("Only COMPLETED or REJECTED statuses are allowed !!"),
    INVALID_APPOINTMENT_UPDATE("Only booked appointments can be updated !!"),
    EARLY_COMPLETION_NOT_ALLOWED("Cannot mark appointment as COMPLETED before scheduled time !!"),

    IMAGE_REQUIRED("Image file is required and cannot be empty."),
    INVALID_IMAGE("Only image files are allowed (jpg, png, webp, etc.)"),
    INVALID_ROLE("Invalid role provided."),
    IMAGE_UPLOAD_FAILED("Failed to upload profile image to Cloudinary.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
