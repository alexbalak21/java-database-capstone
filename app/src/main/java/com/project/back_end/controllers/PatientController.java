package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	 */
	private String extractTokenFromHeader(String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

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

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
		return service.validatePatientLogin(login);
	}

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


