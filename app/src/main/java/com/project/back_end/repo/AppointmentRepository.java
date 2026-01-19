package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Retrieve appointments for a doctor within a given time range.
     * Uses LEFT JOIN FETCH to eagerly load doctor and availability information.
     * 
     * @param doctorId the doctor's ID
     * @param start the start of the time range
     * @param end the end of the time range
     * @return List of appointments for the doctor within the time range
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor LEFT JOIN FETCH a.doctorAvailability WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Filter appointments by doctor ID, partial patient name (case-insensitive), and time range.
     * Uses LEFT JOIN FETCH to include doctor and patient details.
     * 
     * @param doctorId the doctor's ID
     * @param patientName the partial or full patient name (case-insensitive)
     * @param start the start of the time range
     * @param end the end of the time range
     * @return List of filtered appointments
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor LEFT JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Delete all appointments related to a specific doctor.
     * Uses @Modifying and @Transactional to enable the delete operation within a transaction.
     * 
     * @param doctorId the doctor's ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Find all appointments for a specific patient.
     * 
     * @param patientId the patient's ID
     * @return List of appointments for the patient
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Retrieve appointments for a patient by status, ordered by appointment time (ascending).
     * 
     * @param patientId the patient's ID
     * @param status the appointment status
     * @return List of appointments for the patient with the given status, ordered by appointment time
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    /**
     * Search appointments by partial doctor name and patient ID.
     * Uses LOWER and CONCAT for case-insensitive partial matching.
     * 
     * @param doctorName the partial or full doctor name (case-insensitive)
     * @param patientId the patient's ID
     * @return List of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId
    );

    /**
     * Filter appointments by doctor name, patient ID, and status.
     * Uses LOWER, CONCAT, and additional filtering on status for case-insensitive partial matching.
     * 
     * @param doctorName the partial or full doctor name (case-insensitive)
     * @param patientId the patient's ID
     * @param status the appointment status
     * @return List of appointments matching all criteria
     */
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId,
            @Param("status") int status
    );

}

