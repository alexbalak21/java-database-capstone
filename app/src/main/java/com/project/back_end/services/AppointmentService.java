package com.project.back_end.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final com.project.back_end.services.Service appService;
    private final TokenService tokenService;

    /**
     * Constructor injection for all required dependencies.
     */
    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               com.project.back_end.services.Service appService,
                               TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appService = appService;
        this.tokenService = tokenService;
    }

    /**
     * Books a new appointment.
     * 
     * @param appointment the appointment object to book
     * @return 1 if successful, 0 if there's an error
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates an existing appointment.
     * Validates the appointment data and checks if the doctor is available at the specified time.
     * 
     * @param appointment the appointment object to update
     * @return ResponseEntity with a map containing success or error message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Check if appointment exists
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());
            if (existingAppointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validate appointment
            int validationResult = appService.validateAppointment(appointment);
            if (validationResult == -1) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (validationResult == 0) {
                response.put("message", "Selected time is not available");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Check if doctor exists
            Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId());
            if (doctor.isEmpty()) {
                response.put("message", "Invalid doctor ID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save updated appointment
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancels an existing appointment.
     * Ensures the patient attempting to cancel is the one who booked it.
     * 
     * @param id the appointment ID
     * @param token the authorization token
     * @return ResponseEntity with a map containing success or error message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            if (appointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = appointmentOpt.get();
            
            // Verify token and extract patient ID
            Long patientIdFromToken = tokenService.extractPatientId(token);
            if (patientIdFromToken == null) {
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Verify the patient attempting to cancel is the appointment owner
            if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
                response.put("message", "You can only cancel your own appointments");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Delete the appointment
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieves appointments for a specific doctor on a specific date.
     * Optionally filters by patient name.
     * 
     * @param pname the patient name filter (optional)
     * @param date the date to retrieve appointments for
     * @param token the authorization token
     * @return Map containing the list of appointments
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract doctor ID from token
            Long doctorId = tokenService.extractDoctorId(token);
            if (doctorId == null) {
                response.put("success", false);
                response.put("message", "Invalid token");
                return response;
            }

            // Set time boundaries for the date
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.atTime(LocalTime.MAX);

            // Fetch appointments for the doctor on the specified date
            List<Appointment> appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startDateTime, endDateTime);

            // Filter by patient name if provided
            if (pname != null && !pname.isEmpty()) {
                appointments = appointments.stream()
                        .filter(app -> app.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                        .toList();
            }

            response.put("success", true);
            response.put("appointments", appointments);
            response.put("count", appointments.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving appointments: " + e.getMessage());
            return response;
        }
    }

    /**
     * Changes the status of an appointment.
     * 
     * @param id the appointment ID
     * @param newStatus the new status value
     * @return ResponseEntity with a map containing success or error message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(long id, int newStatus) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            if (appointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update the status
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(newStatus);
            appointmentRepository.save(appointment);

            response.put("message", "Appointment status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating appointment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
