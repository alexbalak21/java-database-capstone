package com.project.back_end.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.TokenService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    /**
     * Constructor injection for all required dependencies.
     */
    public DoctorService(DoctorRepository doctorRepository,
                        AppointmentRepository appointmentRepository,
                        TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Fetches the available time slots for a specific doctor on a given date.
     * Filters out already booked slots from the available slots.
     * 
     * @param doctorId the ID of the doctor
     * @param date the date for which availability is needed
     * @return List of available time slots as strings
     */
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            // Define all available time slots for a day
            List<String> allSlots = generateTimeSlots();
            
            // Fetch appointments for the doctor on the specified date
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
            
            List<Appointment> appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startDateTime, endDateTime);
            
            // Extract booked time slots
            List<String> bookedSlots = new ArrayList<>();
            for (Appointment apt : appointments) {
                if (apt.getAppointmentTime() != null) {
                    bookedSlots.add(apt.getAppointmentTime().toLocalTime().toString());
                }
            }
            
            // Filter out booked slots from available slots
            List<String> availableSlots = new ArrayList<>();
            for (String slot : allSlots) {
                if (!bookedSlots.contains(slot)) {
                    availableSlots.add(slot);
                }
            }
            
            return availableSlots;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Generates a list of time slots for a full day.
     * 
     * @return List of time slots
     */
    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 9; hour < 17; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                slots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return slots;
    }

    /**
     * Saves a new doctor to the database.
     * Checks if a doctor with the same email already exists.
     * 
     * @param doctor the doctor object to save
     * @return 1 for success, -1 if doctor already exists, 0 for internal errors
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            // Check if doctor already exists by email
            Optional<Doctor> existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor.isPresent()) {
                return -1; // Doctor already exists
            }
            
            // Save the new doctor
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Updates the details of an existing doctor.
     * 
     * @param doctor the doctor object with updated details
     * @return 1 for success, -1 if doctor not found, 0 for internal errors
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            // Check if doctor exists by ID
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());
            if (existingDoctor.isEmpty()) {
                return -1; // Doctor not found
            }
            
            // Save the updated doctor
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Retrieves all doctors from the database.
     * 
     * @return List of all doctors
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Deletes a doctor by ID along with all associated appointments.
     * 
     * @param id the ID of the doctor to delete
     * @return 1 for success, -1 if doctor not found, 0 for internal errors
     */
    @Transactional
    public int deleteDoctor(long id) {
        try {
            // Check if doctor exists
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return -1; // Doctor not found
            }
            
            // Delete all associated appointments
            appointmentRepository.deleteAllByDoctorId(id);
            
            // Delete the doctor
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Validates a doctor's login credentials.
     * 
     * @param login the login object containing email and password
     * @return ResponseEntity with a token if valid, or an error message if not
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find doctor by email
            Optional<Doctor> doctorOpt = doctorRepository.findByEmail(login.getIdentifier());
            if (doctorOpt.isEmpty()) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Doctor doctor = doctorOpt.get();
            
            // Verify password
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Generate token
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error validating doctor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Finds doctors by partial name match.
     * 
     * @param name the name of the doctor to search for
     * @return Map with the list of doctors matching the name
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            response.put("success", true);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error finding doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters doctors by name, specialty, and availability during AM/PM.
     * 
     * @param name the doctor's name
     * @param specialty the doctor's specialty
     * @param amOrPm the time of day (AM/PM)
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Filter by name and specialty
            List<Doctor> doctors = doctorRepository
                    .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            
            // Filter by time (AM/PM)
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("success", true);
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters doctors by name and their availability during AM/PM.
     * 
     * @param name the doctor's name
     * @param amOrPm the time of day (AM/PM)
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Filter by name
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            
            // Filter by time (AM/PM)
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("success", true);
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters doctors by name and specialty.
     * 
     * @param name the doctor's name
     * @param specialty the doctor's specialty
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository
                    .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            
            response.put("success", true);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters doctors by specialty and their availability during AM/PM.
     * 
     * @param specialty the doctor's specialty
     * @param amOrPm the time of day (AM/PM)
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Filter by specialty
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            
            // Filter by time (AM/PM)
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("success", true);
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters doctors by specialty.
     * 
     * @param specialty the doctor's specialty
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            
            response.put("success", true);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filters all doctors by their availability during AM/PM.
     * 
     * @param amOrPm the time of day (AM/PM)
     * @return Map with the filtered list of doctors
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Fetch all doctors
            List<Doctor> allDoctors = doctorRepository.findAll();
            
            // Filter by time (AM/PM)
            List<Doctor> filteredDoctors = filterDoctorByTime(allDoctors, amOrPm);
            
            response.put("success", true);
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Private helper method to filter a list of doctors by their available times (AM/PM).
     * 
     * @param doctors the list of doctors to filter
     * @param amOrPm the time of day (AM/PM)
     * @return Filtered list of doctors
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filteredDoctors = new ArrayList<>();

        if (amOrPm == null || amOrPm.isEmpty()) {
            return doctors;
        }

        String target = amOrPm.toUpperCase();

        for (Doctor doctor : doctors) {
            if (doctor.getAvailableTimes() == null || doctor.getAvailableTimes().isEmpty()) {
                continue;
            }

            boolean matches = doctor.getAvailableTimes().stream().anyMatch(slot -> {
                try {
                    String start = slot.contains("-") ? slot.split("-")[0].trim() : slot.trim();
                    LocalTime time = LocalTime.parse(start);
                    return ("AM".equals(target) && time.isBefore(LocalTime.NOON))
                            || ("PM".equals(target) && !time.isBefore(LocalTime.NOON));
                } catch (Exception e) {
                    return false;
                }
            });

            if (matches) {
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

}
