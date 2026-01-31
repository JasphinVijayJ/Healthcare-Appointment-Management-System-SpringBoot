package com.healthcare.enums;

public enum ErrorMessage {

    PASSWORD_MISMATCH("Password and Confirm Password do not match."),
    EMAIL_ALREADY_EXISTS("Email is already registered: "),
    INVALID_CREDENTIALS("Invalid email or password."),

    DOCTOR_NOT_FOUND("No doctor found with ID: "),
    PATIENT_NOT_FOUND("No patient found with ID: "),

    DOCTOR_ALREADY_BOOKED("The doctor is already booked for this time slot."),
    PATIENT_ALREADY_BOOKED("You already have an appointment at this time."),

    PAST_TIME("Cannot book an appointment in the past time for today."),
    DOCTOR_NOT_AVAILABLE("Doctor is not available on the selected date, Day: "),
    INVALID_TIME_SLOT("Selected time slot is invalid or unavailable."),
    DAILY_APPOINTMENT_LIMIT("Patient cannot book more than 2 appointments per day."),
    APPOINTMENT_NOT_FOUND("No appointment found with ID: "),

    UNAUTHORIZED_APPOINTMENT_CANCEL("You are not authorized to cancel this appointment."),
    CANNOT_CANCEL_COMPLETED("Cannot cancel a completed appointment."),
    CANNOT_CANCEL_PAST("Cannot cancel appointments scheduled before today."),
    CANNOT_CANCEL_PAST_TODAY("Cannot cancel appointments with a past time today.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
