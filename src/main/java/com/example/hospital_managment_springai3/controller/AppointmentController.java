package com.example.hospital_managment_springai3.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hospital_managment_springai3.dto.AppointmentRequest;
import com.example.hospital_managment_springai3.dto.AppointmentResponse;
import com.example.hospital_managment_springai3.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService service;

	@PostMapping
	public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.book(request));
	}

	@GetMapping
	public ResponseEntity<List<AppointmentResponse>> all() {
		return ResponseEntity.ok(service.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AppointmentResponse> get(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<List<AppointmentResponse>> byPatient(@PathVariable Long patientId) {
		return ResponseEntity.ok(service.getByPatient(patientId));
	}

	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<List<AppointmentResponse>> byDoctor(@PathVariable Long doctorId) {
		return ResponseEntity.ok(service.getByDoctor(doctorId));
	}

	@GetMapping("/date/{date}")
	public ResponseEntity<List<AppointmentResponse>> byDate(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(service.getByDate(date));
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
		return ResponseEntity.ok(service.cancel(id));
	}

	// Note: Emergency Doctor Cancel endpoint for Admin (Option A).
	// Sets all active bookings for a doctor on a specific date to CLINIC_CANCELLED.
	@PatchMapping("/doctor/{doctorId}/cancel-date")
	public ResponseEntity<List<AppointmentResponse>> cancelDoctorDate(
			@PathVariable Long doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(service.cancelDoctorDaySchedule(doctorId, date));
	}

	@PatchMapping("/{id}/no-show")
	public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
		return ResponseEntity.ok(service.markNoShow(id));
	}
}
