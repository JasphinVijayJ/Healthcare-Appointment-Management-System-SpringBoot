package com.healthcare.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.healthcare.dto.common.ApiResponse;
import com.healthcare.dto.contact.ContactRequest;
import com.healthcare.enums.ErrorMessage;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.exception.BadRequestException;
import com.healthcare.exception.ExternalServiceException;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UtilityService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Inject email from application.properties
    @Value("${spring.mail.username}")
    private String adminEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Cloudinary cloudinary;

    public UtilityService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }


    /* ------------------------- Email Service ----------------------------- */

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    public void sendContactEmail(ContactRequest request) {
        String subject = request.getSubject().trim();

        String body = """
                Name : %s
                
                Email : %s
                
                Subject : %s
                
                Message :
                            %s
                """.formatted(
                request.getName().trim(),
                request.getEmail().trim(),
                subject,
                request.getMessage().trim()
        );

        sendEmail(adminEmail, subject, body);
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        sendAppointmentEmail(
                appointment,
                "Appointment Confirmation",
                "Your appointment has been successfully booked.",
                "Please arrive 10 minutes early and carry any previous medical reports.\n\nThank you for choosing our healthcare service.\nWishing you good health \uD83D\uDC96"
        );
    }

    public void sendAppointmentCancellation(Appointment appointment) {
        sendAppointmentEmail(
                appointment,
                "Appointment Cancelled",
                "Your appointment has been cancelled as requested.",
                "If you wish to book another appointment, please visit our platform. For any assistance, feel free to contact our support team.\n\nThank you for choosing our healthcare service.\nWishing you good health \uD83D\uDC96"
        );
    }

    public void sendAppointmentCompleted(Appointment appointment) {
        sendAppointmentEmail(
                appointment,
                "Appointment Completed",
                "Your appointment has been marked as completed successfully.",
                "Thank you for visiting our healthcare service.\nWe wish you good health and a speedy recovery \uD83D\uDC96"
        );
    }

    public void sendAppointmentRejected(Appointment appointment) {
        sendAppointmentEmail(
                appointment,
                "Appointment Rejected",
                "We regret to inform you that your appointment has been rejected.",
                "Please try booking another appointment or contact our support team for assistance."
        );
    }

    private void sendAppointmentEmail(Appointment appointment, String subject,
                                      String introduction, String footer) {
        String body = """
                Dear %s,
                
                %s
                
                Appointment Details
                ---------------------------
                Doctor                     : %s
                Specialty                 : %s
                Date                        : %s
                Time                        : %s
                Consultation Fee     : ₹%.2f
                Status                      : %s
                
                %s
                
                Regards,
                Healthcare Support Team
                Jasphin Vijay J
                """.formatted(
                appointment.getPatient().getName(),
                introduction,
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getDoctor().getConsultationFee(),
                appointment.getStatus(),
                footer
        );

        sendEmail(
                appointment.getPatient().getUser().getEmail(),
                subject,
                body
        );
    }

    /* ------------------------- Profile Image Upload Service ----------------------------- */

    public ApiResponse uploadProfileImage(MultipartFile image, Long loggedInUserId, String role) {
        // Validate file
        if (image == null || image.isEmpty()) {
            throw new BadRequestException(ErrorMessage.IMAGE_REQUIRED.getMessage());
        }

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException(ErrorMessage.INVALID_IMAGE.getMessage());
        }

        try {
            // Check entity exists before upload
            Patient patient = null;
            Doctor doctor = null;

            if (role.equalsIgnoreCase("PATIENT")) {
                patient = patientRepository.findById(loggedInUserId)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND.getMessage() + loggedInUserId));
            }
            else if (role.equalsIgnoreCase("DOCTOR")) {
                doctor = doctorRepository.findById(loggedInUserId)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND.getMessage() + loggedInUserId));
            }
            else {
                throw new BadRequestException(ErrorMessage.INVALID_ROLE.getMessage());
            }

            // Folder Name
            String folder = "HAMS/" + role;

            // Image File Name - FIXED PUBLIC ID (OVERWRITE STRATEGY)
            String publicId = loggedInUserId.toString();

            // Upload (overwrite existing image if exists)
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");

            if (imageUrl == null || imageUrl.isBlank()) {
                throw new ExternalServiceException(ErrorMessage.IMAGE_UPLOAD_FAILED.getMessage());
            }

            // Save image URL in DB
            if (patient != null) {
                patient.setImageUrl(imageUrl);
                patientRepository.save(patient);
            }
            else if (doctor != null) {
                doctor.setImageUrl(imageUrl);
                doctorRepository.save(doctor);
            }

            return new ApiResponse(imageUrl, SuccessMessage.PROFILE_IMAGE_UPLOADED_SUCCESS.getMessage());

        } catch (IOException e) {
            throw new ExternalServiceException(ErrorMessage.IMAGE_UPLOAD_FAILED.getMessage(), e);
        }
    }

}
