package com.project.back_end.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.project.back_end.model.Prescription;
import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Find prescriptions associated with a specific appointment.
     * 
     * @param appointmentId the appointment ID
     * @return List of prescriptions for the specified appointment
     */
    List<Prescription> findByAppointmentId(Long appointmentId);

}