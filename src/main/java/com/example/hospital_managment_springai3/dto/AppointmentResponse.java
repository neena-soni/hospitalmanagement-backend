package com.example.hospital_managment_springai3.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.hospital_managment_springai3.entity.Appointment;
import com.example.hospital_managment_springai3.entity.AppointmentStatus;



public record AppointmentResponse(
		Long id,
		LocalDate appointmentDate,
		String reason,
		AppointmentStatus status,
		LocalDateTime createdAt,
		Long patientId,
		String patientName,
		Long doctorId,
		String doctorName,
		String specialty) {

	public static AppointmentResponse from(Appointment a) {
		return new AppointmentResponse(
				a.getId(), a.getAppointmentDate(), a.getReason(), a.getStatus(), a.getCreatedAt(),
				a.getPatient().getId(), a.getPatient().getName(),
				a.getDoctor().getId(), a.getDoctor().getName(), a.getDoctor().getSpecialty());
	}
}