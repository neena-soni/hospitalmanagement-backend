package com.example.hospital_managment_springai3.dto;

import java.time.LocalDate;

import com.example.hospital_managment_springai3.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientRequestDTO(
		@NotBlank(message = "Patient name is required")
		String name,

		@Min(value = 0, message = "Age cannot be negative")
		@Max(value = 150, message = "Age cannot exceed 150 years")
		Integer age,

		@NotNull(message = "Gender is required")
		Gender gender,

		@NotBlank(message = "Phone number is required")
		@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
		String phoneNumber,

		@Email(message = "Email must be a valid email address")
		String email,

		@Past(message = "Date of birth must be in the past")
		LocalDate dateOfBirth,

		@Size(max = 2000, message = "Medical history must be under 2000 characters")
		String medicalHistory
) {
}
