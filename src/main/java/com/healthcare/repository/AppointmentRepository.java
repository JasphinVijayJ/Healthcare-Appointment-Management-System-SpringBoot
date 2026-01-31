package com.healthcare.repository;

import com.healthcare.dto.appointment.PatientAppointmentResponse;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(Doctor doctor, LocalDate date, LocalTime time, List<AppointmentStatus> status);

    boolean existsByPatientAndAppointmentDateAndAppointmentTimeAndStatusIn(Patient patient, LocalDate date, LocalTime time, List<AppointmentStatus> status);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusIn(Long doctorId, LocalDate date, List<AppointmentStatus> status);

    int countByPatientAndAppointmentDateAndStatus(Patient patient, LocalDate date, AppointmentStatus status);

    @Query("""
            SELECT new com.healthcare.dto.appointment.PatientAppointmentResponse(
            a.id,
            a.patient.id,
            a.appointmentDate,
            a.appointmentTime,
            a.status,
            
            d.id,
            d.name,
            d.specialty,
            d.consultationFee,
            d.imageUrl
            )
            FROM Appointment a JOIN a.doctor d
            WHERE a.patient.id = :patientId
            ORDER BY a.createdAt DESC
            """)
    List<PatientAppointmentResponse> findAppointmentsForPatient(@Param("patientId") Long patientId);
}
