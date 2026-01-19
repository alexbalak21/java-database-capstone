package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Appointment Data Transfer Object (DTO)
 *
 * This class represents appointment data exchanged between backend services
 * and frontend clients. It intentionally avoids persistence annotations.
 */
public class AppointmentDTO {

	private final Long id;
	private final Long doctorId;
	private final String doctorName;
	private final Long patientId;
	private final String patientName;
	private final String patientEmail;
	private final String patientPhone;
	private final String patientAddress;
	private final LocalDateTime appointmentTime;
	private final int status;

	// Computed fields
	private final LocalDate appointmentDate;
	private final LocalTime appointmentTimeOnly;
	private final LocalDateTime endTime;

	/**
	 * Constructs an AppointmentDTO with core fields and computes derived values.
	 *
	 * @param id               unique identifier for the appointment
	 * @param doctorId         ID of the doctor assigned to the appointment
	 * @param doctorName       full name of the doctor
	 * @param patientId        ID of the patient
	 * @param patientName      full name of the patient
	 * @param patientEmail     email address of the patient
	 * @param patientPhone     contact number of the patient
	 * @param patientAddress   residential address of the patient
	 * @param appointmentTime  full date and time of the appointment
	 * @param status           appointment status (e.g., scheduled, completed)
	 */
	public AppointmentDTO(
			Long id,
			Long doctorId,
			String doctorName,
			Long patientId,
			String patientName,
			String patientEmail,
			String patientPhone,
			String patientAddress,
			LocalDateTime appointmentTime,
			int status
	) {
		this.id = id;
		this.doctorId = doctorId;
		this.doctorName = doctorName;
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientEmail = patientEmail;
		this.patientPhone = patientPhone;
		this.patientAddress = patientAddress;
		this.appointmentTime = appointmentTime;
		this.status = status;

		if (appointmentTime != null) {
			this.appointmentDate = appointmentTime.toLocalDate();
			this.appointmentTimeOnly = appointmentTime.toLocalTime();
			this.endTime = appointmentTime.plusHours(1);
		} else {
			this.appointmentDate = null;
			this.appointmentTimeOnly = null;
			this.endTime = null;
		}
	}

	public Long getId() {
		return id;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public Long getPatientId() {
		return patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public String getPatientPhone() {
		return patientPhone;
	}

	public String getPatientAddress() {
		return patientAddress;
	}

	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}

	public int getStatus() {
		return status;
	}

	public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public LocalTime getAppointmentTimeOnly() {
		return appointmentTimeOnly;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}
}
