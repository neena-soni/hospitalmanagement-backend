package com.example.hospital_managment_springai3.entity;



import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctors")
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Specialty is required")
	@Column(nullable = false)
	private String specialty;

	@NotBlank(message = "Phone number is required")
	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Email(message = "Invalid email")
	@Column(unique = true)
	private String email;

	// Note: In future, use soft delete (like boolean deleted = false) instead of deleting
	// the doctor completely, so past appointments still keep their doctor info.

	/** false = doctor is on leave / not taking appointments */
	@Builder.Default
	private Boolean available = true;

	public Boolean isAvailable() {
		return available != null && available;
	}

	@Builder.Default
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "doctor_available_days", joinColumns = @JoinColumn(name = "doctor_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week")
	private Set<DayOfWeek> availableDays = new HashSet<>();

	
	private LocalTime shiftStartTime;
	private LocalTime shiftEndTime;

	@PositiveOrZero(message = "Fee cannot be negative")
	private Double consultationFee;
}