package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.back_end.models.Patient;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email address.
     * 
     * @param email the patient's email address
     * @return the Patient with the specified email, or empty if not found
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Find a patient using either their email or phone number.
     * Provides flexibility for patient identification and validation.
     * 
     * @param email the patient's email address
     * @param phone the patient's phone number
     * @return the Patient with the specified email or phone, or empty if not found
     */
    Optional<Patient> findByEmailOrPhone(String email, String phone);

}