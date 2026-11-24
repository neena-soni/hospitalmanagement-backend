package com.example.hospital_managment_springai3.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import com.example.hospital_managment_springai3.entity.Doctor;

public record DoctorResponseDTO(
		Long id,
		String name,
		String specialty,
		String phoneNumber,
		String email,
		Boolean available,
		Set<DayOfWeek> availableDays,
		LocalTime shiftStartTime,
		LocalTime shiftEndTime,
		Double consultationFee
) {
	public static DoctorResponseDTO from(Doctor d) {
		if (d == null) return null;
		return new DoctorResponseDTO(
				d.getId(),
				d.getName(),
				d.getSpecialty(),
				d.getPhoneNumber(),
				d.getEmail(),
				d.isAvailable(),
				d.getAvailableDays(),
				d.getShiftStartTime(),
				d.getShiftEndTime(),
				d.getConsultationFee()
		);
	}
}
