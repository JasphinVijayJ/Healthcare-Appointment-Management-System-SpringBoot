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

    int countByPatientAndAppointmentDateAndStatusIn(Patient patient, LocalDate date, List<AppointmentStatus> status);

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


    @Query("""
            SELECT COALESCE(SUM(a.doctorFee), 0)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.status = 'COMPLETED'
            """)
    double getTotalEarningsByDoctor(@Param("doctorId") Long doctorId);


    long countByDoctor_Id(Long doctorId);


    @Query("""
            SELECT COUNT(DISTINCT a.patient.id)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            """)
    long countDistinctPatientsByDoctor(@Param("doctorId") Long doctorId);


    @Query(value = """
            SELECT *
            FROM appointments a
            WHERE a.doctor_id = :doctorId
            ORDER BY
            CASE
                 WHEN a.status = 'BOOKED' THEN 0
                 ELSE 1
            END,
            a.created_at DESC
            LIMIT 6
            """, nativeQuery = true)
    List<Appointment> findTop6RecentAppointmentsForDoctorDashboard(@Param("doctorId") Long doctorId);


    @Query(value = """
            SELECT *
            FROM appointments a
            WHERE a.doctor_id = :doctorId
            ORDER BY
            CASE
                 WHEN a.status = 'BOOKED' THEN 0
                 ELSE 1
            END,
            a.created_at DESC
            """, nativeQuery = true)
    List<Appointment> findAppointmentsForDoctorAppointments(@Param("doctorId") Long doctorId);

}
