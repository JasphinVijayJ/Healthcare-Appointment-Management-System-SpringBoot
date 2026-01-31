package com.healthcare.dto.doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailableDayResponse {

    private final Long doctorId;
    private final LocalDate date;
    private final List<LocalTime> timeSlots;
    private final Integer slotDuration;

    public AvailableDayResponse(Long doctorId, LocalDate date, List<LocalTime> timeSlots, Integer slotDuration) {
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlots = timeSlots;
        this.slotDuration = slotDuration;
    }

    // Getters
    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<LocalTime> getTimeSlots() {
        return timeSlots;
    }

    public Integer getSlotDuration() {
        return slotDuration;
    }
}
