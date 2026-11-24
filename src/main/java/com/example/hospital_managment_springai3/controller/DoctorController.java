package com.example.hospital_managment_springai3.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hospital_managment_springai3.dto.CapacityResponse;
import com.example.hospital_managment_springai3.dto.DoctorRequestDTO;
import com.example.hospital_managment_springai3.dto.DoctorResponseDTO;
import com.example.hospital_managment_springai3.service.AppointmentService;
import com.example.hospital_managment_springai3.service.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

	private final DoctorService doctorService;
	private final AppointmentService appointmentService;

	@PostMapping
	public ResponseEntity<DoctorResponseDTO> create(@Valid @RequestBody DoctorRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<DoctorResponseDTO>> list(
			@RequestParam(required = false) String specialty,
			@RequestParam(defaultValue = "false") boolean availableOnly) {
		return ResponseEntity.ok(doctorService.search(specialty, availableOnly));
	}

	@GetMapping("/specialties")
	public ResponseEntity<List<String>> specialties() {
		return ResponseEntity.ok(doctorService.getSpecialties());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DoctorResponseDTO> get(@PathVariable Long id) {
		return ResponseEntity.ok(doctorService.getById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DoctorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DoctorRequestDTO request) {
		return ResponseEntity.ok(doctorService.update(id, request));
	}

	@PatchMapping("/{id}/availability")
	public ResponseEntity<DoctorResponseDTO> setAvailability(@PathVariable Long id, @RequestParam boolean available) {
		return ResponseEntity.ok(doctorService.setAvailable(id, available));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		doctorService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// per day per shift only 10 spots for a single doctor
	@GetMapping("/{id}/capacity")
	public ResponseEntity<CapacityResponse> capacity(@PathVariable Long id,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(appointmentService.getCapacity(id, date));
	}

	// BATCH CAPACITY: Returns capacities for all doctors on a specific date in ONE single call
	@GetMapping("/capacity")
	public ResponseEntity<java.util.Map<Long, CapacityResponse>> batchCapacity(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(appointmentService.getBatchCapacity(date));
	}
}
