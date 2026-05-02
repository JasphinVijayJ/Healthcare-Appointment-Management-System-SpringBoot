package com.healthcare.service;

import com.healthcare.dto.appointment.AppointmentRequest;
import com.healthcare.dto.appointment.PatientAppointmentResponse;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.enums.ErrorMessage;
import com.healthcare.exception.AppointmentValidationException;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.exception.SlotAlreadyBookedException;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private static final int MAX_SLOTS_PER_DAY = 6;
    private static final int MAX_DAILY_PATIENT_APPOINTMENTS = 2;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final UtilityService utilityService;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, DoctorAvailabilityRepository doctorAvailabilityRepository, UtilityService utilityService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.utilityService = utilityService;
    }

    @Transactional
    public void bookAppointment(AppointmentRequest request) {
        // Fetch doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + request.getDoctorId()));

        // Fetch patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND.getMessage() + request.getPatientId()));

        LocalDate appointmentDate = request.getAppointmentDate();
        LocalTime appointmentTime = request.getAppointmentTime();

        // Past date validation is handled by @FutureOrPresent annotation in AppointmentRequest DTO
        // Past time validation for today
        if (appointmentDate.isEqual(LocalDate.now()) && !appointmentTime.isAfter(LocalTime.now()))
            throw new AppointmentValidationException(ErrorMessage.PAST_TIME.getMessage());

        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();

        // Doctor not available on this day
        DoctorAvailability doctorAvailability = doctorAvailabilityRepository.findByDoctor_IdAndDayOfWeekAndIsActiveTrue(request.getDoctorId(), dayOfWeek)
                .orElseThrow(() -> new AppointmentValidationException(ErrorMessage.DOCTOR_NOT_AVAILABLE.getMessage() + dayOfWeek));

        // Generate valid slots for the day
        List<LocalTime> validSlots = CommonUtil.generateTimeSlots(
                doctorAvailability.getStartTime(),
                doctorAvailability.getEndTime(),
                doctorAvailability.getSlotDuration(),
                MAX_SLOTS_PER_DAY
        );

        // Check if requested time matches a valid slot
        if (!validSlots.contains(appointmentTime))
            throw new AppointmentValidationException(ErrorMessage.INVALID_TIME_SLOT.getMessage());

        // Block BOOKED & COMPLETED slots
        List<AppointmentStatus> blockedStatuses =
                List.of(AppointmentStatus.BOOKED, AppointmentStatus.COMPLETED);

        // Doctor already booked
        if (appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
                doctor, appointmentDate, appointmentTime, blockedStatuses)) {
            throw new SlotAlreadyBookedException(ErrorMessage.DOCTOR_ALREADY_BOOKED.getMessage());
        }

        // Patient already booked
        if (appointmentRepository.existsByPatientAndAppointmentDateAndAppointmentTimeAndStatusIn(
                patient, appointmentDate, appointmentTime, blockedStatuses)) {
            throw new SlotAlreadyBookedException(ErrorMessage.PATIENT_ALREADY_BOOKED.getMessage());
        }

        // Check daily limit for patient appointments
        int dailyCount = appointmentRepository.countByPatientAndAppointmentDateAndStatus(patient, appointmentDate, AppointmentStatus.BOOKED);
        if (dailyCount >= MAX_DAILY_PATIENT_APPOINTMENTS)
            throw new AppointmentValidationException(ErrorMessage.DAILY_APPOINTMENT_LIMIT.getMessage());

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        appointmentRepository.save(appointment);

        utilityService.sendAppointmentConfirmation(appointment);
    }

    public List<PatientAppointmentResponse> getAppointmentsForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId))
            throw new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND.getMessage() + patientId);

        return appointmentRepository.findAppointmentsForPatient(patientId);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND.getMessage() + appointmentId));

        // Validate appointment belongs to requesting patient
        if (!appointment.getPatient().getId().equals(patientId))
            throw new AppointmentValidationException(ErrorMessage.UNAUTHORIZED_APPOINTMENT_CANCEL.getMessage());

        // Already cancelled → nothing to do
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) return;

        // Prevent cancelling completed appointments
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new AppointmentValidationException(ErrorMessage.CANNOT_CANCEL_COMPLETED.getMessage());

        // Prevent cancelling past appointments (date before today)
        if (appointment.getAppointmentDate().isBefore(LocalDate.now()))
            throw new AppointmentValidationException(ErrorMessage.CANNOT_CANCEL_PAST.getMessage());

        // Prevent cancelling past time slots today
        if (appointment.getAppointmentDate().isEqual(LocalDate.now()) &&
                appointment.getAppointmentTime().isBefore(LocalTime.now()))
            throw new AppointmentValidationException(ErrorMessage.CANNOT_CANCEL_PAST_TODAY.getMessage());

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        utilityService.sendAppointmentCancellation(appointment);
    }
}
