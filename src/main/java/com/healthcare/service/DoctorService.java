package com.healthcare.service;

import com.healthcare.dto.appointment.LatestAppointmentResponse;
import com.healthcare.dto.doctor.AvailableDayResponse;
import com.healthcare.dto.doctor.DoctorDashboardResponse;
import com.healthcare.dto.doctor.DoctorListResponse;
import com.healthcare.dto.doctor.DoctorProfileResponse;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.enums.ErrorMessage;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorAvailabilityRepository doctorAvailabilityRepository, AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<DoctorListResponse> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorListResponse> response = new ArrayList<>();

        for (Doctor doctor : doctors) {
            response.add(
                    new DoctorListResponse(
                            doctor.getId(),
                            doctor.getName(),
                            doctor.getSpecialty(),
                            doctor.getQualifications(),
                            doctor.getExperience(),
                            doctor.getImageUrl()
                    )
            );
        }
        return response;
    }

    public DoctorProfileResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + id));

        return new DoctorProfileResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getQualifications(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getLanguages(),
                doctor.getImageUrl()
        );
    }

    public List<AvailableDayResponse> getDoctorAvailabilityForNextWeek(Long doctorId) {
        // Check if doctor exists
        if (!doctorRepository.existsById(doctorId))
            throw new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + doctorId);

        List<AvailableDayResponse> response = new ArrayList<>();
        LocalDate date = LocalDate.now();
        int maxDaysChecked = 0;

        final int MAX_SLOTS_PER_DAY = 6;
        final int AVAILABLE_DAYS_LIMIT = 6;
        final int MAX_FUTURE_SEARCH_DAYS = 30;

        while (response.size() < AVAILABLE_DAYS_LIMIT && maxDaysChecked < MAX_FUTURE_SEARCH_DAYS) {
            // Skip Sunday (doctor leave day)
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                Optional<DoctorAvailability> optionalDoctorAvailability = doctorAvailabilityRepository.findByDoctor_IdAndDayOfWeekAndIsActiveTrue(doctorId, dayOfWeek);

                if (optionalDoctorAvailability.isPresent()) {
                    DoctorAvailability doctorAvailability = optionalDoctorAvailability.get();

                    // Generate all slots
                    List<LocalTime> slots = CommonUtil.generateTimeSlots(
                            doctorAvailability.getStartTime(),
                            doctorAvailability.getEndTime(),
                            doctorAvailability.getSlotDuration(),
                            MAX_SLOTS_PER_DAY
                    );

                    // Fetch BLOCKED appointments (BOOKED + COMPLETED)
                    List<AppointmentStatus> blockedStatuses =
                            List.of(AppointmentStatus.BOOKED, AppointmentStatus.COMPLETED);

                    // Get booked appointments
                    List<Appointment> appointments = appointmentRepository.findByDoctor_IdAndAppointmentDateAndStatusIn(
                            doctorId, date, blockedStatuses);

                    // Extract booked times
                    Set<LocalTime> bookedTimeSlots = new HashSet<>();
                    for (Appointment a : appointments)
                        bookedTimeSlots.add(a.getAppointmentTime());

                    // Remove booked slots
                    slots.removeAll(bookedTimeSlots);

                    // Remove past slots for today
                    if (date.equals(LocalDate.now())) {
                        LocalTime now = LocalTime.now();
                        slots.removeIf(slot -> !slot.isAfter(now));
                    }

                    if (!slots.isEmpty()) {
                        response.add(new AvailableDayResponse(doctorId, date, slots, doctorAvailability.getSlotDuration()));
                    }
                }
            }
            // Move to next day
            date = date.plusDays(1);
            maxDaysChecked++;
        }
        return response;
    }

    public DoctorDashboardResponse getDoctorDashboard(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + id));

        List<Appointment> appointments = appointmentRepository.findTop6RecentAppointmentsForDoctorDashboard(id);

        List<LatestAppointmentResponse> response = new ArrayList<>();

        for (Appointment appointment : appointments)
        {
            Patient patient = appointment.getPatient();

            response.add(
                    new LatestAppointmentResponse(
                            appointment.getId(),
                            patient.getName(),
                            CommonUtil.calculateAge(patient.getDob()),
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus()
                    )
            );
        }


        return new DoctorDashboardResponse(
                doctor.getName(),
                appointmentRepository.getTotalEarningsByDoctor(id),
                appointmentRepository.countByDoctor_Id(id),
                appointmentRepository.countDistinctPatientsByDoctor(id),
                response
        );
    }
}
