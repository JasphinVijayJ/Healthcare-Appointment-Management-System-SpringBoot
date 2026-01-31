package com.healthcare.repository;

import com.healthcare.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    Optional<DoctorAvailability> findByDoctor_IdAndDayOfWeekAndIsActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}
