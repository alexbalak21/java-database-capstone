package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;
	private final Service service;

	public PatientController(PatientService patientService, Service service) {
		this.patientService = patientService;
		this.service = service;
	}

	/**
	 * Extract JWT token from Authorization header
	 * Expected format: "Bearer <token>"
	 * 
	 * @param authHeader Authorization header string
	 * @return JWT token string without "Bearer " prefix, or null if invalid
	 */
	private String extractTokenFromHeader(String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

	/**
	 * Get patient details by JWT token
	 * 
	 * @param authHeader Authorization header containing JWT token (format: "Bearer <token>")
	 * @return ResponseEntity with patient details (id, name, email, phone, address) or error message
	 *         - 200 OK with patient object on success
	 *         - 401 UNAUTHORIZED if token is missing or invalid
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> getPatient(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(validation.getStatusCode()).body((Map) validation.getBody());
		}
		return patientService.getPatientDetails(token);
	}

	/**
	 * Create a new patient account (signup)
	 * 
	 * @param patient Patient object with name, email, phone, password, age, address, gender
	 * @return ResponseEntity with message
	 *         - 201 CREATED with {"message": "Signup successful"} on success
	 *         - 409 CONFLICT with {"message": "Patient with email id or phone no already exist"} if duplicate
	 *         - 500 INTERNAL_SERVER_ERROR with {"message": "Internal server error"} on failure
	 */
	@PostMapping
	public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
		boolean valid = service.validatePatient(patient);
		if (!valid) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Patient with email id or phone no already exist"));
		}

		int result = patientService.createPatient(patient);
		if (result == 1) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Signup successful"));
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("message", "Internal server error"));
	}

	/**
	 * Authenticate patient and generate JWT token
	 * 
	 * @param login Login DTO with identifier (email) and password
	 * @return ResponseEntity with message and token
	 *         - 200 OK with {"message": "Login successful", "token": "<JWT>"} on success
	 *         - 401 UNAUTHORIZED with {"message": "Patient not found"} or {"message": "Invalid password"} on failure
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
		return service.validatePatientLogin(login);
	}

	/**
	 * Get all appointments for a specific patient
	 * 
	 * @param authHeader Authorization header containing JWT token (format: "Bearer <token>")
	 * @param id Patient ID
	 * @return ResponseEntity with appointments list
	 *         - 200 OK with {"appointments": [...]} containing appointment details (id, date, time, doctor name, status)
	 *         - 401 UNAUTHORIZED if token is missing or invalid
	 */
	@GetMapping("/appointments")
	public ResponseEntity<Map<String, Object>> getPatientAppointment(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestParam Long id) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(validation.getStatusCode()).body((Map) validation.getBody());
		}

		return patientService.getPatientAppointment(id, token);
	}

	/**
	 * Filter patient appointments by condition and/or doctor name
	 * 
	 * @param authHeader Authorization header containing JWT token (format: "Bearer <token>")
	 * @param condition Filter by appointment status (e.g., "pending", "confirmed", "completed")
	 * @param name Filter by doctor name
	 * @return ResponseEntity with filtered appointments list
	 *         - 200 OK with {"appointments": [...]} containing filtered appointment details
	 *         - 401 UNAUTHORIZED if token is missing or invalid
	 */
	@GetMapping("/filter")
	public ResponseEntity<Map<String, Object>> filterPatientAppointment(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestParam(required = false) String condition,
			@RequestParam(required = false) String name) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		return service.filterPatient(condition, name, token);
	}

}


