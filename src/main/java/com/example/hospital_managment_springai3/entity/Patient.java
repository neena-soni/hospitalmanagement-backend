package com.example.hospital_managment_springai3.entity;



import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patients")
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@Min(value = 0, message = "Age cannot be negative")
	@Max(value = 150, message = "Invalid age")
	private Integer age;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@NotBlank(message = "Phone number is required")
	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Email(message = "Invalid email")
	@Column(unique = true)
	private String email;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@Size(max = 2000, message = "Medical history must be under 2000 characters")
	@Column(length = 2000)
	private String medicalHistory;
}