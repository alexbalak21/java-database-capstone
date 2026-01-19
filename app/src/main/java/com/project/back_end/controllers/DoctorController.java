package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

	private final DoctorService doctorService;
	private final Service service;

	public DoctorController(DoctorService doctorService, Service service) {
		this.doctorService = doctorService;
		this.service = service;
	}

	@GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
	public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
																	 @PathVariable Long doctorId,
																	 @PathVariable String date,
																	 @PathVariable String token) {
		ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
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

	@PostMapping("/{token}")
	public ResponseEntity<Map<String, String>> saveDoctor(@PathVariable String token,
														  @RequestBody Doctor doctor) {
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

	@PutMapping("/{token}")
	public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable String token,
															@RequestBody Doctor doctor) {
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

	@DeleteMapping("/{id}/{token}")
	public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable long id,
															@PathVariable String token) {
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

	@GetMapping("/filter/{name}/{time}/{speciality}")
	public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable String name,
															 @PathVariable String time,
															 @PathVariable String speciality) {
		Map<String, Object> response = service.filterDoctor(name, speciality, time);
		return ResponseEntity.ok(response);
	}

}
