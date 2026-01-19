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
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping("/{date}/{patientName}/{token}")
	public ResponseEntity<?> getAppointments(@PathVariable String date,
											 @PathVariable String patientName,
											 @PathVariable String token) {
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

	@PostMapping("/{token}")
	public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
															   @RequestBody Appointment appointment) {
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

	@PutMapping("/{token}")
	public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
																 @RequestBody Appointment appointment) {
		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		return appointmentService.updateAppointment(appointment);
	}

	@DeleteMapping("/{id}/{token}")
	public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
																 @PathVariable String token) {
		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		return appointmentService.cancelAppointment(id, token);
	}

}
