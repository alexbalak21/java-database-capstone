package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exceptions.InvalidTokenException;
import com.project.back_end.exceptions.TokenExpiredException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

	private static final Logger log = LoggerFactory.getLogger(PatientService.class);

	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;
	private final TokenService tokenService;

	public PatientService(PatientRepository patientRepository,
						  AppointmentRepository appointmentRepository,
						  TokenService tokenService) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
		this.tokenService = tokenService;
	}

	/**
	 * Creates a new patient.
	 *
	 * @param patient patient to persist
	 * @return 1 on success, 0 otherwise
	 */
	@Transactional
	public int createPatient(Patient patient) {
		try {
			patientRepository.save(patient);
			return 1;
		} catch (Exception e) {
			log.error("Failed to create patient", e);
			return 0;
		}
	}

	/**
	 * Retrieves appointments for a patient, ensuring the token belongs to that patient.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
		Map<String, Object> response = new HashMap<>();

		try {
			String email = tokenService.extractEmail(token);
			Optional<Patient> patientOpt = patientRepository.findByEmail(email);

			if (patientOpt.isEmpty()) {
				response.put("message", "Patient not found");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			Patient patientFromToken = patientOpt.get();
			if (!patientFromToken.getId().equals(id)) {
				response.put("message", "Unauthorized patient access");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id)
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());

			response.put("success", true);
			response.put("appointments", appointments);
			response.put("count", appointments.size());
			return ResponseEntity.ok(response);
		} catch (TokenExpiredException | InvalidTokenException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} catch (Exception e) {
			log.error("Error fetching patient appointments", e);
			response.put("message", "Error fetching appointments");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Filters appointments by condition (past/future) for a patient.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
		Map<String, Object> response = new HashMap<>();

		int status;
		if ("past".equalsIgnoreCase(condition)) {
			status = 1;
		} else if ("future".equalsIgnoreCase(condition)) {
			status = 0;
		} else {
			response.put("message", "Invalid condition. Use 'past' or 'future'");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			List<AppointmentDTO> appointments = appointmentRepository
					.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status)
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());

			response.put("success", true);
			response.put("appointments", appointments);
			response.put("count", appointments.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error filtering appointments by condition", e);
			response.put("message", "Error filtering appointments");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Filters appointments by doctor name for a patient.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<AppointmentDTO> appointments = appointmentRepository
					.filterByDoctorNameAndPatientId(name, patientId)
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());

			response.put("success", true);
			response.put("appointments", appointments);
			response.put("count", appointments.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error filtering appointments by doctor", e);
			response.put("message", "Error filtering appointments");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Filters appointments by doctor name and condition for a patient.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
		Map<String, Object> response = new HashMap<>();

		int status;
		if ("past".equalsIgnoreCase(condition)) {
			status = 1;
		} else if ("future".equalsIgnoreCase(condition)) {
			status = 0;
		} else {
			response.put("message", "Invalid condition. Use 'past' or 'future'");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			List<AppointmentDTO> appointments = appointmentRepository
					.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status)
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());

			response.put("success", true);
			response.put("appointments", appointments);
			response.put("count", appointments.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error filtering appointments by doctor and condition", e);
			response.put("message", "Error filtering appointments");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Retrieves patient details using the provided token.
	 */
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
		Map<String, Object> response = new HashMap<>();

		try {
			String email = tokenService.extractEmail(token);
			Optional<Patient> patientOpt = patientRepository.findByEmail(email);

			if (patientOpt.isEmpty()) {
				response.put("message", "Patient not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			Patient patient = patientOpt.get();
			Map<String, Object> patientData = new HashMap<>();
			patientData.put("id", patient.getId());
			patientData.put("name", patient.getName());
			patientData.put("email", patient.getEmail());
			patientData.put("phone", patient.getPhone());
			patientData.put("address", patient.getAddress());

			response.put("success", true);
			response.put("patient", patientData);
			return ResponseEntity.ok(response);
		} catch (TokenExpiredException | InvalidTokenException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} catch (Exception e) {
			log.error("Error retrieving patient details", e);
			response.put("message", "Error retrieving patient details");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	private AppointmentDTO toDto(Appointment appointment) {
		if (appointment == null) {
			return null;
		}

		Patient patient = appointment.getPatient();
		Doctor doctor = appointment.getDoctor();

		return new AppointmentDTO(
				appointment.getId(),
				doctor != null ? doctor.getId() : null,
				doctor != null ? doctor.getName() : null,
				patient != null ? patient.getId() : null,
				patient != null ? patient.getName() : null,
				patient != null ? patient.getEmail() : null,
				patient != null ? patient.getPhone() : null,
				patient != null ? patient.getAddress() : null,
				appointment.getAppointmentTime(),
				appointment.getStatus() != null ? appointment.getStatus() : 0
		);
	}
}
