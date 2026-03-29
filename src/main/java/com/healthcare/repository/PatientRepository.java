package com.healthcare.repository;

import com.healthcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("select id from Patient where user.id = :userId")
    Optional<Long> findPatientIdByUserId(@Param("userId") Long userId);
}
