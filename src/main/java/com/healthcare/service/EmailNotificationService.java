package com.healthcare.service;

import com.healthcare.dto.contact.ContactRequest;
import com.healthcare.model.Appointment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final EmailService emailService;

    // Inject email from application.properties
    @Value("${spring.mail.username}")
    private String adminEmail;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
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
        emailService.sendEmail(adminEmail, subject, body);
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Appointment Confirmation";

        String body = """
                Dear %s,
                
                Your appointment has been successfully booked.
                
                Appointment Details
                ---------------------------
                Doctor      : %s
                Specialty  : %s
                Date         : %s
                Time         : %s
                Consultation Fee : ₹%.2f
                Status      : %s
                
                Please arrive 10 minutes early and carry any previous medical reports.
                
                Thank you for choosing our healthcare service.
                Wishing you good health!
                
                Regards,
                Healthcare Support Team
                """.formatted(
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getDoctor().getConsultationFee(),
                appointment.getStatus()
        );
        emailService.sendEmail(
                appointment.getPatient().getUser().getEmail(),
                subject,
                body
        );
    }

    public void sendAppointmentCancellation(Appointment appointment) {
        String subject = "Appointment Cancelled";

        String body = """
                Dear %s,
                
                Your appointment has been successfully cancelled.
                
                Appointment Details
                ---------------------------
                Doctor      : %s
                Specialty  : %s
                Date         : %s
                Time         : %s
                Consultation Fee : ₹%.2f
                Status      : %s
                
                If you wish to book another appointment, please visit our platform.
                For any assistance, feel free to contact our support team.
                
                Thank you for choosing our healthcare service.
                Wishing you good health!
                
                Regards,
                Healthcare Support Team
                """.formatted(
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getDoctor().getConsultationFee(),
                appointment.getStatus()
        );

        emailService.sendEmail(
                appointment.getPatient().getUser().getEmail(),
                subject,
                body
        );
    }
}
