package com.healthcare.dto.doctor;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorScheduleResponse {

    private final Long id;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer slotDuration;
    private final Boolean isActive;

    public DoctorScheduleResponse(Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Integer slotDuration, Boolean isActive) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDuration = slotDuration;
        this.isActive = isActive;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getSlotDuration() {
        return slotDuration;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}
