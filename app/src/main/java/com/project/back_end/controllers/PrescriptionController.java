package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

	private static final int COMPLETED_STATUS = 1;

	private final PrescriptionService prescriptionService;
	private final Service service;
	private final AppointmentService appointmentService;

	public PrescriptionController(PrescriptionService prescriptionService,
								  Service service,
								  AppointmentService appointmentService) {
		this.prescriptionService = prescriptionService;
		this.service = service;
		this.appointmentService = appointmentService;
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

	@PostMapping
	public ResponseEntity<Map<String, String>> savePrescription(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@Valid @RequestBody Prescription prescription) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			Map<String, String> error = new HashMap<>(validation.getBody());
			return ResponseEntity.status(validation.getStatusCode()).body(error);
		}

		ResponseEntity<Map<String, String>> saveResponse = prescriptionService.savePrescription(prescription);
		if (!saveResponse.getStatusCode().is2xxSuccessful()) {
			return saveResponse;
		}

		ResponseEntity<Map<String, String>> statusResponse = appointmentService
				.changeStatus(prescription.getAppointmentId(), COMPLETED_STATUS);
		if (!statusResponse.getStatusCode().is2xxSuccessful()) {
			return statusResponse;
		}

		return saveResponse;
	}

	@GetMapping("/{appointmentId}")
	public ResponseEntity<Map<String, Object>> getPrescription(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable Long appointmentId) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			Map<String, Object> error = new HashMap<>();
			error.put("message", "Missing or invalid Authorization header");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			Map<String, Object> error = new HashMap<>();
			error.putAll(validation.getBody());
			return ResponseEntity.status(validation.getStatusCode()).body(error);
		}

		return prescriptionService.getPrescription(appointmentId);
	}
}
