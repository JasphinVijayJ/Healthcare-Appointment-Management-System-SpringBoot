package com.healthcare.service;

import com.healthcare.dto.auth.LoginRequest;
import com.healthcare.dto.auth.LoginResponse;
import com.healthcare.dto.auth.RegisterRequest;
import com.healthcare.enums.*;
import com.healthcare.exception.EmailAlreadyExistsException;
import com.healthcare.exception.InvalidCredentialsException;
import com.healthcare.exception.PasswordMismatchException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final JwtUtil jwtUtil;

    @Value("${cloudinary.default-doctor-image}")
    private String defaultDoctorImage;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
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

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));

        // Verify password
        if (!request.getPassword().equals(user.getPassword())) {
            throw new InvalidCredentialsException(ErrorMessage.INVALID_CREDENTIALS.getMessage());
        }

        // Find PatientId By using UserId
        Long patientID = patientRepository.findPatientIdByUserId(user.getId())
                .orElseThrow(() -> new InvalidCredentialsException(ErrorMessage.PATIENT_NOT_FOUND.getMessage()));

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(patientID, user.getEmail(), user.getRole(), token, SuccessMessage.LOGIN_SUCCESS.getMessage());
    }
}
