package com.example.hospital_managment_springai3.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital_managment_springai3.entity.Patient;



public interface PatientRepository extends JpaRepository<Patient, Long> {

	List<Patient> findByNameContainingIgnoreCase(String name);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}