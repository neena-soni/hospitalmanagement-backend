package com.example.hospital_managment_springai3.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hospital_managment_springai3.dto.PatientRequestDTO;
import com.example.hospital_managment_springai3.dto.PatientResponseDTO;
import com.example.hospital_managment_springai3.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService service;

	@PostMapping
	public ResponseEntity<PatientResponseDTO> create(@Valid @RequestBody PatientRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
	}

	@GetMapping
	public ResponseEntity<List<PatientResponseDTO>> list(@RequestParam(required = false) String name) {
		return ResponseEntity.ok(service.search(name));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PatientResponseDTO> get(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<PatientResponseDTO>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}

	@PutMapping("/{id}")
	public ResponseEntity<PatientResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PatientRequestDTO request) {
		return ResponseEntity.ok(service.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
