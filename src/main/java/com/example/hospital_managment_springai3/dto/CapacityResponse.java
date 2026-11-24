package com.example.hospital_managment_springai3.dto;


import java.time.LocalDate;

public record CapacityResponse(
		Long doctorId,
		String doctorName,
		String specialty,
		LocalDate date,
		int maxPerDay,
		long booked,
		long remaining,
		boolean canBook,
		String message) {
}