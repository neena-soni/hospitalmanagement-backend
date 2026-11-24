package com.example.hospital_managment_springai3.dto;



import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentRequest(
		@NotNull(message = "Patient id is required")
		Long patientId,
		@NotNull(message = "Doctor id is required") 
		Long doctorId,
		@NotNull(message = "Appointment date is required")
		@FutureOrPresent(message = "Appointment date cannot be in the past") 
		LocalDate appointmentDate,
		@NotBlank(message = "Reason is required")
		@Size(max = 1000, message = "Reason must be under 1000 characters")
		String reason) {
}