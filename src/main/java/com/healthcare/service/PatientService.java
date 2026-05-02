package com.healthcare.service;

import com.healthcare.dto.patient.PatientDetailsResponse;
import com.healthcare.enums.ErrorMessage;
import com.healthcare.enums.SuccessMessage;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.model.Patient;
import com.healthcare.repository.PatientRepository;
import com.healthcare.util.CommonUtil;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientDetailsResponse getPatientById(Long id) {
       Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND.getMessage() + id));

        return new PatientDetailsResponse(
                patient.getName(),
                patient.getUser().getEmail(),
                patient.getUser().getRole(),
                patient.getPhone(),
                patient.getDob(),
                patient.getAddress(),
                patient.getBloodGroup(),
                CommonUtil.getAllBloodGroups(),
                patient.getGender(),
                CommonUtil.getAllGenders(),
                patient.getImageUrl(),
                SuccessMessage.PROFILE_UPDATED_SUCCESS.getMessage()
        );
    }

    public PatientDetailsResponse updatePatientProfile(Long id, Patient request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND.getMessage() + id));

        patient.setName(request.getName());
        patient.setPhone(request.getPhone());
        patient.setDob(request.getDob());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAddress(request.getAddress());

        patientRepository.save(patient);

        return new PatientDetailsResponse(
                patient.getName(),
                patient.getUser().getEmail(),
                patient.getUser().getRole(),
                patient.getPhone(),
                patient.getDob(),
                patient.getAddress(),
                patient.getBloodGroup(),
                CommonUtil.getAllBloodGroups(),
                patient.getGender(),
                CommonUtil.getAllGenders(),
                patient.getImageUrl(),
                SuccessMessage.PROFILE_UPDATED_SUCCESS.getMessage()
        );
    }
}
