package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionService {

	private static final Logger log = LoggerFactory.getLogger(PrescriptionService.class);

	private final PrescriptionRepository prescriptionRepository;

	public PrescriptionService(PrescriptionRepository prescriptionRepository) {
		this.prescriptionRepository = prescriptionRepository;
	}

	/**
	 * Saves a prescription if one does not already exist for the appointment.
	 */
	@Transactional
	public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
		Map<String, String> response = new HashMap<>();

		try {
			List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
			if (existing != null && !existing.isEmpty()) {
				response.put("message", "Prescription already exists for this appointment");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			prescriptionRepository.save(prescription);
			response.put("message", "Prescription saved");
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			log.error("Error saving prescription", e);
			response.put("message", "Error saving prescription");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Retrieves prescription(s) by appointment id.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);

			if (prescriptions == null || prescriptions.isEmpty()) {
				response.put("message", "No prescription found for this appointment");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			response.put("success", true);
			response.put("prescriptions", prescriptions);
			response.put("count", prescriptions.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error retrieving prescription", e);
			response.put("message", "Error retrieving prescription");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
