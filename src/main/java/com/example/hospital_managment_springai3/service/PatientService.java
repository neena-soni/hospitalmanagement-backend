package com.example.hospital_managment_springai3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital_managment_springai3.dto.PatientRequestDTO;
import com.example.hospital_managment_springai3.dto.PatientResponseDTO;
import com.example.hospital_managment_springai3.entity.Patient;
import com.example.hospital_managment_springai3.exception.ResourceNotFoundException;
import com.example.hospital_managment_springai3.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

	private final PatientRepository repo;

	@Transactional
	public PatientResponseDTO create(PatientRequestDTO dto) {
		if (repo.existsByPhoneNumber(dto.phoneNumber())) {
			throw new IllegalArgumentException("Duplicate phone number: a patient with phone number " + dto.phoneNumber() + " already exists");
		}
		if (dto.email() != null && !dto.email().isBlank() && repo.existsByEmailIgnoreCase(dto.email().trim())) {
			throw new IllegalArgumentException("Duplicate email: a patient with email " + dto.email() + " already exists");
		}
		Patient p = toEntity(dto);
		p.setId(null);
		return toResponse(repo.save(p));
	}

	@Transactional
	public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
		Patient existing = getPatient(id);
		if (repo.existsByPhoneNumberAndIdNot(dto.phoneNumber(), id)) {
			throw new IllegalArgumentException("Duplicate phone number: another patient with phone number " + dto.phoneNumber() + " already exists");
		}
		if (dto.email() != null && !dto.email().isBlank() && repo.existsByEmailIgnoreCaseAndIdNot(dto.email().trim(), id)) {
			throw new IllegalArgumentException("Duplicate email: another patient with email " + dto.email() + " already exists");
		}
		existing.setName(dto.name());
		existing.setAge(dto.age());
		existing.setGender(dto.gender());
		existing.setPhoneNumber(dto.phoneNumber());
		existing.setEmail(dto.email());
		existing.setDateOfBirth(dto.dateOfBirth());
		existing.setMedicalHistory(dto.medicalHistory());
		return toResponse(repo.save(existing));
	}

	@Transactional
	public void delete(Long id) {
		repo.delete(getPatient(id));
	}

	public Patient getPatient(Long id) {
		return repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
	}

	public PatientResponseDTO getById(Long id) {
		return toResponse(getPatient(id));
	}

	public List<PatientResponseDTO> getAll() {
		return repo.findAll().stream().map(this::toResponse).toList();
	}

	public List<PatientResponseDTO> search(String name) {
		List<Patient> list = (name == null || name.isBlank()) 
				? repo.findAll() 
				: repo.findByNameContainingIgnoreCase(name.trim());
		return list.stream().map(this::toResponse).toList();
	}

	public Patient toEntity(PatientRequestDTO dto) {
		if (dto == null) return null;
		return Patient.builder()
				.name(dto.name())
				.age(dto.age())
				.gender(dto.gender())
				.phoneNumber(dto.phoneNumber())
				.email(dto.email())
				.dateOfBirth(dto.dateOfBirth())
				.medicalHistory(dto.medicalHistory())
				.build();
	}

	public PatientResponseDTO toResponse(Patient p) {
		return PatientResponseDTO.from(p);
	}
}
