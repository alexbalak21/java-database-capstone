package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Service {

	private static final Logger log = LoggerFactory.getLogger(Service.class);

	private final TokenService tokenService;
	private final AdminRepository adminRepository;
	private final DoctorRepository doctorRepository;
	private final PatientRepository patientRepository;
	private final DoctorService doctorService;
	private final PatientService patientService;

	public Service(TokenService tokenService,
				   AdminRepository adminRepository,
				   DoctorRepository doctorRepository,
				   PatientRepository patientRepository,
				   DoctorService doctorService,
				   PatientService patientService) {
		this.tokenService = tokenService;
		this.adminRepository = adminRepository;
		this.doctorRepository = doctorRepository;
		this.patientRepository = patientRepository;
		this.doctorService = doctorService;
		this.patientService = patientService;
	}

	/**
	 * Validates a JWT token for a given user role.
	 */
	public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
		Map<String, String> response = new HashMap<>();
		boolean valid = tokenService.validateToken(token, user);

		if (!valid) {
			response.put("message", "Invalid or expired token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		response.put("message", "Valid token");
		return ResponseEntity.ok(response);
	}

	/**
	 * Validates admin credentials and returns a token on success.
	 */
	public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
		Map<String, String> response = new HashMap<>();

		try {
			Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
			if (admin == null) {
				response.put("message", "Admin not found");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
				response.put("message", "Invalid credentials");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			String token = tokenService.generateToken(admin.getUsername());
			response.put("message", "Login successful");
			response.put("token", token);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error validating admin", e);
			response.put("message", "Error validating admin");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Filters doctors based on provided criteria.
	 */
	public Map<String, Object> filterDoctor(String name, String specialty, String time) {
		// Normalize inputs to avoid null checks downstream
		String safeName = name != null ? name : "";
		String safeSpecialty = specialty != null ? specialty : "";
		String safeTime = time != null ? time : "";

		// Choose appropriate filter path based on provided parameters
		if (!safeName.isEmpty() && !safeSpecialty.isEmpty() && !safeTime.isEmpty()) {
			return doctorService.filterDoctorsByNameSpecilityandTime(safeName, safeSpecialty, safeTime);
		}
		if (!safeName.isEmpty() && !safeTime.isEmpty()) {
			return doctorService.filterDoctorByNameAndTime(safeName, safeTime);
		}
		if (!safeName.isEmpty() && !safeSpecialty.isEmpty()) {
			return doctorService.filterDoctorByNameAndSpecility(safeName, safeSpecialty);
		}
		if (!safeSpecialty.isEmpty() && !safeTime.isEmpty()) {
			return doctorService.filterDoctorByTimeAndSpecility(safeSpecialty, safeTime);
		}
		if (!safeSpecialty.isEmpty()) {
			return doctorService.filterDoctorBySpecility(safeSpecialty);
		}
		if (!safeTime.isEmpty()) {
			return doctorService.filterDoctorsByTime(safeTime);
		}

		// Default: no filters, return all doctors
		Map<String, Object> response = new HashMap<>();
		try {
			List<?> doctors = doctorRepository.findAll();
			response.put("success", true);
			response.put("doctors", doctors);
			response.put("count", doctors.size());
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error retrieving doctors: " + e.getMessage());
		}
		return response;
	}

	/**
	 * Validates if an appointment time is available for the doctor.
	 * Returns 1 if available, 0 if time unavailable, -1 if doctor not found.
	 */
	public int validateAppointment(Appointment appointment) {
		try {
			Optional<?> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
			if (doctorOpt.isEmpty()) {
				return -1;
			}

			LocalDate date = appointment.getAppointmentTime().toLocalDate();
			String time = appointment.getAppointmentTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

			List<String> availableSlots = doctorService.getDoctorAvailability(appointment.getDoctor().getId(), date);
			return availableSlots.contains(time) ? 1 : 0;
		} catch (Exception e) {
			log.error("Error validating appointment", e);
			return 0;
		}
	}

	/**
	 * Validates uniqueness of patient by email or phone.
	 */
	public boolean validatePatient(Patient patient) {
		try {
			Optional<Patient> existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
			return existing.isEmpty();
		} catch (Exception e) {
			log.error("Error validating patient", e);
			return false;
		}
	}

	/**
	 * Validates patient login and returns a token on success.
	 */
	public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
		Map<String, String> response = new HashMap<>();

		try {
			Optional<Patient> patientOpt = patientRepository.findByEmail(login.getIdentifier());
			if (patientOpt.isEmpty()) {
				response.put("message", "Patient not found");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			Patient patient = patientOpt.get();
			if (!patient.getPassword().equals(login.getPassword())) {
				response.put("message", "Invalid credentials");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			String token = tokenService.generateToken(patient.getEmail());
			response.put("message", "Login successful");
			response.put("token", token);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error validating patient login", e);
			response.put("message", "Error validating patient login");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Filters a patient's appointments based on condition and/or doctor name.
	 */
	public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
		try {
			String email = tokenService.extractEmail(token);
			Optional<Patient> patientOpt = patientRepository.findByEmail(email);
			if (patientOpt.isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("message", "Patient not found");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			Long patientId = patientOpt.get().getId();

			if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
				return patientService.filterByDoctorAndCondition(condition, name, patientId);
			}
			if (condition != null && !condition.isEmpty()) {
				return patientService.filterByCondition(condition, patientId);
			}
			if (name != null && !name.isEmpty()) {
				return patientService.filterByDoctor(name, patientId);
			}

			// Default to returning all appointments for the patient
			return patientService.getPatientAppointment(patientId, token);
		} catch (Exception e) {
			Map<String, Object> response = new HashMap<>();
			log.error("Error filtering patient appointments", e);
			response.put("message", "Error filtering appointments");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
