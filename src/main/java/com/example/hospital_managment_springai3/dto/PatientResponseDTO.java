package com.example.hospital_managment_springai3.dto;

import java.time.LocalDate;

import com.example.hospital_managment_springai3.entity.Gender;
import com.example.hospital_managment_springai3.entity.Patient;

public record PatientResponseDTO(
		Long id,
		String name,
		Integer age,
		Gender gender,
		String phoneNumber,
		String email,
		LocalDate dateOfBirth,
		String medicalHistory
) {
	public static PatientResponseDTO from(Patient p) {
		if (p == null) return null;
		return new PatientResponseDTO(
				p.getId(),
				p.getName(),
				p.getAge(),
				p.getGender(),
				p.getPhoneNumber(),
				p.getEmail(),
				p.getDateOfBirth(),
				p.getMedicalHistory()
		);
	}
}
