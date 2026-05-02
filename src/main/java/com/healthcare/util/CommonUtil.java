package com.healthcare.util;

import com.healthcare.enums.BloodGroup;
import com.healthcare.enums.Gender;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommonUtil {

    private CommonUtil() {
        throw new UnsupportedOperationException("Object creation not allowed for utility class");
    }

    /**
     * Generate time slots between start and end times
     *
     * @param start           - starting time
     * @param end             - ending time
     * @param durationMinutes - duration of each slot in minutes
     * @param maxSlotsPerDay  - maximum number of slots to generate per day
     * @return list of slots as strings in HH:mm format
     */

    public static List<LocalTime> generateTimeSlots(LocalTime start, LocalTime end, int durationMinutes, int maxSlotsPerDay) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = start;

        while (!current.plusMinutes(durationMinutes).isAfter(end) && slots.size() < maxSlotsPerDay) {
            slots.add(current);
            current = current.plusMinutes(durationMinutes);
        }
        return slots;
    }

    public static List<String> getAllBloodGroups() {
        return Arrays.stream(BloodGroup.values())
                .map(BloodGroup::getValue)
                .toList();
    }

    public static List<String> getAllGenders() {
        return Arrays.stream(Gender.values())
                .map(Enum::name)
                .toList();
    }
}
