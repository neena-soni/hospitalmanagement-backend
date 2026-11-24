package com.example.hospital_managment_springai3.dto;



import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/** Doctor details + how many of the 10 daily spots are taken on the requested date. */
public record DoctorInfo(
		Long id,
		String name,
		String specialty,
		Set<DayOfWeek> availableDays,
		LocalTime shiftStartTime,
		LocalTime shiftEndTime,
		Double consultationFee,
		LocalDate date,
		long booked,
		long spotsLeft,
		boolean canBook,
		String note) {
}