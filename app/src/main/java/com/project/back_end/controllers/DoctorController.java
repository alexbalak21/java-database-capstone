package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping("${api.path}doctor")
public class DoctorController {

	private final DoctorService doctorService;
	private final Service service;
	private final TokenService tokenService;

	public DoctorController(DoctorService doctorService, Service service, TokenService tokenService) {
		this.doctorService = doctorService;
		this.service = service;
		this.tokenService = tokenService;
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

	@GetMapping("/availability/{doctorId}/{date}")
	public ResponseEntity<Map<String, Object>> getDoctorAvailability(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable Long doctorId,
			@PathVariable String date) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(validation.getStatusCode()).body(new HashMap<>(validation.getBody()));
		}

		try {
			LocalDate parsedDate = LocalDate.parse(date);
			List<String> availability = doctorService.getDoctorAvailability(doctorId, parsedDate);
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("availability", availability);
			response.put("count", availability.size());
			return ResponseEntity.ok(response);
		} catch (DateTimeParseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Invalid date format. Use ISO-8601 (yyyy-MM-dd)"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error retrieving availability"));
		}
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getDoctors() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Doctor> doctors = doctorService.getDoctors();
			response.put("success", true);
			response.put("doctors", doctors);
			response.put("count", doctors.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error fetching doctors");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> saveDoctor(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestBody Doctor doctor) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		int result = doctorService.saveDoctor(doctor);
		if (result == 1) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Doctor added to db"));
		} else if (result == -1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Doctor already exists"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Some internal error occurred"));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
		return doctorService.validateDoctor(login);
	}

	@PutMapping
	public ResponseEntity<Map<String, String>> updateDoctor(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestBody Doctor doctor) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		int result = doctorService.updateDoctor(doctor);
		if (result == 1) {
			return ResponseEntity.ok(Map.of("message", "Doctor updated"));
		} else if (result == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Doctor not found"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Some internal error occurred"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteDoctor(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable long id) {
		
		String token = extractTokenFromHeader(authHeader);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Missing or invalid Authorization header"));
		}

		ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
		if (!validation.getStatusCode().is2xxSuccessful()) {
			return validation;
		}

		int result = doctorService.deleteDoctor(id);
		if (result == 1) {
			return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
		} else if (result == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Doctor not found with id"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Some internal error occurred"));
		}
	}

	@GetMapping("/filter")
	public ResponseEntity<Map<String, Object>> filterDoctors(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String time,
			@RequestParam(required = false) String specialty) {
		// Treat "any" as no filter for specialty
		if ("any".equalsIgnoreCase(specialty)) {
			specialty = null;
		}
		// Treat "any" as no filter for time
		if ("any".equalsIgnoreCase(time)) {
			time = null;
		}
		Map<String, Object> response = service.filterDoctor(name, specialty, time);
		return ResponseEntity.ok(response);
	}
}
