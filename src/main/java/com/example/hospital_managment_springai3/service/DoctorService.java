package com.example.hospital_managment_springai3.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital_managment_springai3.dto.DoctorRequestDTO;
import com.example.hospital_managment_springai3.dto.DoctorResponseDTO;
import com.example.hospital_managment_springai3.entity.Doctor;
import com.example.hospital_managment_springai3.exception.ResourceNotFoundException;
import com.example.hospital_managment_springai3.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

	private final DoctorRepository repo;

	@Transactional
	public DoctorResponseDTO create(DoctorRequestDTO dto) {
		validateShift(dto.shiftStartTime(), dto.shiftEndTime());
		validateWorkingDays(dto.availableDays());
		if (repo.existsByPhoneNumber(dto.phoneNumber())) {
			throw new IllegalArgumentException("Duplicate phone number: a doctor with phone number " + dto.phoneNumber() + " already exists");
		}
		if (dto.email() != null && !dto.email().isBlank() && repo.existsByEmailIgnoreCase(dto.email().trim())) {
			throw new IllegalArgumentException("Duplicate email: a doctor with email " + dto.email() + " already exists");
		}
		Doctor d = toEntity(dto);
		d.setId(null);
		return toResponse(repo.save(d));
	}

	@Transactional
	public DoctorResponseDTO update(Long id, DoctorRequestDTO dto) {
		Doctor existing = getEntity(id);
		validateShift(dto.shiftStartTime(), dto.shiftEndTime());
		validateWorkingDays(dto.availableDays());
		if (repo.existsByPhoneNumberAndIdNot(dto.phoneNumber(), id)) {
			throw new IllegalArgumentException("Duplicate phone number: another doctor with phone number " + dto.phoneNumber() + " already exists");
		}
		if (dto.email() != null && !dto.email().isBlank() && repo.existsByEmailIgnoreCaseAndIdNot(dto.email().trim(), id)) {
			throw new IllegalArgumentException("Duplicate email: another doctor with email " + dto.email() + " already exists");
		}
		existing.setName(dto.name());
		existing.setSpecialty(dto.specialty());
		existing.setPhoneNumber(dto.phoneNumber());
		existing.setEmail(dto.email());
		if (dto.available() != null) {
			existing.setAvailable(dto.available());
		}
		existing.setAvailableDays(dto.availableDays() != null ? dto.availableDays() : new HashSet<>());
		existing.setShiftStartTime(dto.shiftStartTime());
		existing.setShiftEndTime(dto.shiftEndTime());
		existing.setConsultationFee(dto.consultationFee());
		return toResponse(repo.save(existing));
	}

	@Transactional
	public DoctorResponseDTO setAvailable(Long id, boolean available) {
		Doctor d = getEntity(id);
		d.setAvailable(available);
		return toResponse(repo.save(d));
	}

	@Transactional
	public void delete(Long id) {
		repo.delete(getEntity(id));
	}

	public Doctor getEntity(Long id) {
		return repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
	}

	public DoctorResponseDTO getById(Long id) {
		return toResponse(getEntity(id));
	}

	public List<DoctorResponseDTO> search(String specialty, boolean onlyAvailable) {
		List<Doctor> doctors = searchEntities(specialty, onlyAvailable);
		return doctors.stream().map(this::toResponse).toList();
	}

	public List<Doctor> searchEntities(String specialty, boolean onlyAvailable) {
		boolean hasSpecialty = specialty != null && !specialty.isBlank();
		if (hasSpecialty && onlyAvailable) return repo.findBySpecialtyIgnoreCaseAndAvailableTrue(specialty.trim());
		if (hasSpecialty) return repo.findBySpecialtyIgnoreCase(specialty.trim());
		if (onlyAvailable) return repo.findByAvailableTrue();
		return repo.findAll();
	}

	public List<String> getSpecialties() {
		return repo.findDistinctSpecialties();
	}

	private void validateWorkingDays(java.util.Set<java.time.DayOfWeek> days) {
		if (days == null || days.isEmpty()) {
			throw new IllegalArgumentException("Doctor must have at least one working day selected");
		}
	}

	private void validateShift(java.time.LocalTime start, java.time.LocalTime end) {
		if (start == null) {
			throw new IllegalArgumentException("Shift start time is required");
		}
		if (end == null) {
			throw new IllegalArgumentException("Shift end time is required");
		}
		if (!end.isAfter(start)) {
			throw new IllegalArgumentException("Shift end time (" + end + ") must be after shift start time (" + start + ")");
		}
	}

	public Doctor toEntity(DoctorRequestDTO dto) {
		if (dto == null) return null;
		return Doctor.builder()
				.name(dto.name())
				.specialty(dto.specialty())
				.phoneNumber(dto.phoneNumber())
				.email(dto.email())
				.available(dto.available() != null ? dto.available() : true)
				.availableDays(dto.availableDays() != null ? dto.availableDays() : new HashSet<>())
				.shiftStartTime(dto.shiftStartTime())
				.shiftEndTime(dto.shiftEndTime())
				.consultationFee(dto.consultationFee())
				.build();
	}

	public DoctorResponseDTO toResponse(Doctor d) {
		return DoctorResponseDTO.from(d);
	}
}
