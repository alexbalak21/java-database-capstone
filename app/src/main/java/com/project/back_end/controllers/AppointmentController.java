package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;
	private final Service service;

	public AppointmentController(AppointmentService appointmentService, Service service) {
		this.appointmentService = appointmentService;
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
	public ResponseEntity<?> getAppointments(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestParam String date,
			@RequestParam String patientName) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		// validate token for doctor role
		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		LocalDate parsedDate;
		try {
			parsedDate = LocalDate.parse(date);
		} catch (DateTimeParseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Invalid date format. Use ISO-8601 (yyyy-MM-dd)"));
		}

		Map<String, Object> result = appointmentService.getAppointment(patientName, parsedDate, token);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> bookAppointment(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestBody Appointment appointment) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		int validationResult = service.validateAppointment(appointment);
		if (validationResult == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Doctor not found"));
		}
		if (validationResult == 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Selected time is not available"));
		}

		int booked = appointmentService.bookAppointment(appointment);
		if (booked == 1) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Appointment booked successfully"));
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("message", "Error booking appointment"));
	}

	@PutMapping
	public ResponseEntity<Map<String, String>> updateAppointment(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestBody Appointment appointment) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		return appointmentService.updateAppointment(appointment);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> cancelAppointment(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable long id) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		return appointmentService.cancelAppointment(id, token);
	}

}
