package com.healthcare.repository;

import com.healthcare.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctor_IdOrderByIdAsc(Long doctorId);

    Optional<DoctorAvailability> findByDoctor_IdAndDayOfWeekAndIsActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}
