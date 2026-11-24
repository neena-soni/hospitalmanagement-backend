package com.example.hospital_managment_springai3.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record DoctorRequestDTO(
		@NotBlank(message = "Doctor name is required")
		String name,

		@NotBlank(message = "Specialty is required")
		String specialty,

		@NotBlank(message = "Phone number is required")
		@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
		String phoneNumber,

		@Email(message = "Email must be a valid email address")
		String email,

		Boolean available,

		Set<DayOfWeek> availableDays,

		@NotNull(message = "Shift start time is required")
		@JsonFormat(pattern = "HH:mm[:ss]")
		LocalTime shiftStartTime,

		@NotNull(message = "Shift end time is required")
		@JsonFormat(pattern = "HH:mm[:ss]")
		LocalTime shiftEndTime,

		@NotNull(message = "Consultation fee is required")
		@PositiveOrZero(message = "Consultation fee cannot be negative")
		Double consultationFee
) {
	public DoctorRequestDTO {
		if (available == null) {
			available = true;
		}
	}
}
