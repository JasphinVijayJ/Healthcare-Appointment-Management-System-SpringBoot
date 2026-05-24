package com.healthcare.service;

import com.healthcare.dto.auth.LoginRequest;
import com.healthcare.dto.auth.LoginResponse;
import com.healthcare.dto.auth.RegisterRequest;
import com.healthcare.enums.*;
import com.healthcare.exception.EmailAlreadyExistsException;
import com.healthcare.exception.InvalidCredentialsException;
import com.healthcare.exception.PasswordMismatchException;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final JwtUtil jwtUtil;

    @Value("${cloudinary.default-doctor-image}")
    private String defaultDoctorImage;

    @Value("${cloudinary.default-patient-image}")
    private String defaultPatientImage;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            DoctorAvailabilityRepository doctorAvailabilityRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void registerPatient(RegisterRequest request) {

        validateRegisterRequest(request);

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.PATIENT);
        userRepository.save(user);

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setGender(Gender.NOT_SPECIFIED);
        patient.setDob(LocalDate.of(2002, 10, 3));
        patient.setPhone("0000000000");
        patient.setAddress("Not set");
        patient.setBloodGroup(BloodGroup.UNKNOWN.getValue());
        patient.setImageUrl(defaultPatientImage);

        patient.setUser(user);
        patientRepository.save(patient);
    }

    @Transactional
    public void registerDoctor(RegisterRequest request) {

        validateRegisterRequest(request);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.DOCTOR);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setSpecialty("Not set");
        doctor.setExperience(0);
        doctor.setQualifications("Not set");
        doctor.setConsultationFee(1.0);
        doctor.setPhone("0000000000");
        doctor.setLanguages("Not set");
        doctor.setImageUrl(defaultDoctorImage);

        doctor.setUser(user);
        doctorRepository.save(doctor);

        List<DoctorAvailability> schedules = new ArrayList<>();

        for (DayOfWeek day : List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        )) {
            DoctorAvailability schedule = new DoctorAvailability();

            schedule.setDayOfWeek(day);
            schedule.setStartTime(LocalTime.of(10, 0, 0));
            schedule.setEndTime(LocalTime.of(17, 0, 0));
            schedule.setDoctor(doctor);
            schedule.setIsActive(false);
            schedule.setSlotDuration(30);

            schedules.add(schedule);
        }

        doctorAvailabilityRepository.saveAll(schedules);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException(ErrorMessage.PASSWORD_MISMATCH.getMessage());
        }

        String email = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(ErrorMessage.EMAIL_ALREADY_EXISTS.getMessage() + email);
        }
        request.setEmail(email);
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));

        // Verify password
        if (!request.getPassword().equals(user.getPassword())) {
            throw new InvalidCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage());
        }

        Long id = null;

        if (Role.PATIENT == user.getRole()) {
            id = patientRepository.findPatientIdByUserId(user.getId())
                    .orElseThrow(() -> new InvalidCredentialsException(ErrorMessage.PATIENT_NOT_FOUND.getMessage()));
        } else if (Role.DOCTOR == user.getRole()) {
            id = doctorRepository.findDoctorIdByUserId(user.getId())
                    .orElseThrow(() -> new InvalidCredentialsException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage()));
        }

        // Generate JWT
        String token = jwtUtil.generateToken(id, user.getEmail(), user.getRole().name());

        // Create HttpOnly Cookie
        Cookie cookie = new Cookie("jwt", token);

        cookie.setHttpOnly(true);   // JS cannot access this
        cookie.setSecure(false);    // set true in production (requires HTTPS)
        cookie.setPath("/");        // cookie sent on all routes
        // cookie.setMaxAge(24 * 60 * 60); // 1 day in seconds
        cookie.setMaxAge(15 * 60); // 15 minutes
        // cookie.setMaxAge(15); // auto logout after 15 seconds

        response.addCookie(cookie); // attach to response

        return new LoginResponse(user.getEmail(), user.getRole(), SuccessMessage.LOGIN_SUCCESS.getMessage());
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // maxAge 0 = delete the cookie

        response.addCookie(cookie);
    }
}
