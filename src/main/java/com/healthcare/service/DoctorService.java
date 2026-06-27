package com.healthcare.service;

import com.healthcare.dto.appointment.LatestAppointmentResponse;
import com.healthcare.dto.doctor.*;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.enums.ErrorMessage;
import com.healthcare.enums.SuccessMessage;
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

    public DoctorDetailsResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + id));

        return new DoctorDetailsResponse(
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


        List<DoctorAvailability> schedules = doctorAvailabilityRepository.findByDoctor_IdOrderByIdAsc(doctorId);

        Map<DayOfWeek, DoctorAvailability> scheduleMap = new HashMap<>();

        for (DoctorAvailability s : schedules) {
            scheduleMap.put(s.getDayOfWeek(), s);
        }


        List<AvailableDayResponse> response = new ArrayList<>();

        LocalDate date = LocalDate.now();
        int maxDaysChecked = 0;

        final int MAX_VISIBLE_SLOTS = 6;
        final int AVAILABLE_DAYS_LIMIT = 6;
        final int MAX_FUTURE_SEARCH_DAYS = 30;

        while (response.size() < AVAILABLE_DAYS_LIMIT && maxDaysChecked < MAX_FUTURE_SEARCH_DAYS) {

            DayOfWeek dayOfWeek = date.getDayOfWeek();

            DoctorAvailability availability = scheduleMap.get(dayOfWeek);

            if (availability != null && Boolean.TRUE.equals(availability.getIsActive())) {

                // Generate all slots
                List<LocalTime> slots = CommonUtil.generateTimeSlots(
                        availability.getStartTime(),
                        availability.getEndTime(),
                        availability.getSlotDuration()
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

                slots = slots.stream()
                        .limit(MAX_VISIBLE_SLOTS)
                        .toList();

                if (!slots.isEmpty()) {
                    response.add(new AvailableDayResponse(doctorId, date, slots, availability.getSlotDuration()));
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

        List<Appointment> top6Appointments = appointmentRepository.findTop6RecentAppointmentsForDoctorDashboard(id);

        List<LatestAppointmentResponse> response = new ArrayList<>();

        for (Appointment appointment : top6Appointments) {
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


        Object[] status = appointmentRepository.getDoctorDashboardStatsCount(id).getFirst();

        String earnings = String.format("%.0f/%.0f",
                ((Number) status[0]).doubleValue(),
                ((Number) status[1]).doubleValue());

        String appointments = status[2] + "/" + status[3];
        String patients = status[4] + "/" + status[5];
        String completedAppointments = status[6].toString();
        String appointmentStatus = status[7] + "/" + status[8];


        return new DoctorDashboardResponse(
                doctor.getName(),
                earnings,
                appointments,
                patients,
                completedAppointments,
                appointmentStatus,
                response
        );
    }

    public DoctorMyProfileResponse getDoctorProfile(Long loggedInUserId) {
        Doctor doctor = doctorRepository.findById(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + loggedInUserId));

        return new DoctorMyProfileResponse(
                doctor.getName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getRole(),
                doctor.getPhone(),
                doctor.getSpecialty(),
                doctor.getQualifications(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getLanguages(),
                doctor.getImageUrl(),
                SuccessMessage.PROFILE_FETCHED_SUCCESS.getMessage()
        );
    }

    public DoctorMyProfileResponse updateDoctorProfile(Long loggedInUserId, Doctor request) {
        Doctor doctor = doctorRepository.findById(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + loggedInUserId));

        doctor.setName(request.getName());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setQualifications(request.getQualifications());
        doctor.setExperience(request.getExperience());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setLanguages(request.getLanguages());

        doctorRepository.save(doctor);

        return new DoctorMyProfileResponse(
                doctor.getName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getRole(),
                doctor.getPhone(),
                doctor.getSpecialty(),
                doctor.getQualifications(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getLanguages(),
                doctor.getImageUrl(),
                SuccessMessage.PROFILE_UPDATED_SUCCESS.getMessage()
        );
    }

    public List<DoctorScheduleResponse> getDoctorSchedule(Long doctorId) {
        // Check if doctor exists
        if (!doctorRepository.existsById(doctorId))
            throw new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + doctorId);

        return doctorAvailabilityRepository.findByDoctor_IdOrderByIdAsc(doctorId)
                .stream()
                .map(avail -> new DoctorScheduleResponse(
                        avail.getId(),
                        avail.getDayOfWeek(),
                        avail.getStartTime(),
                        avail.getEndTime(),
                        avail.getSlotDuration(),
                        avail.getIsActive()
                ))
                .toList();
    }

    public List<DoctorScheduleResponse> updateDoctorSchedule(Long doctorId, List<DoctorAvailability> requestList) {
        // Check if doctor exists
        if (!doctorRepository.existsById(doctorId))
            throw new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + doctorId);


        List<DoctorAvailability> updatedList = new ArrayList<>();

        for (DoctorAvailability req : requestList) {
            DoctorAvailability availability = doctorAvailabilityRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.SCHEDULE_NOT_FOUND.getMessage() + req.getId()));

            availability.setDayOfWeek(req.getDayOfWeek());
            availability.setStartTime(req.getStartTime());
            availability.setEndTime(req.getEndTime());
            availability.setSlotDuration(req.getSlotDuration());
            availability.setIsActive(req.getIsActive());

            updatedList.add(availability);
        }

        doctorAvailabilityRepository.saveAll(updatedList);


        return updatedList.stream()
                .map(avail -> new DoctorScheduleResponse(
                        avail.getId(),
                        avail.getDayOfWeek(),
                        avail.getStartTime(),
                        avail.getEndTime(),
                        avail.getSlotDuration(),
                        avail.getIsActive()
                ))
                .toList();
    }

}
