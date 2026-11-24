package com.example.hospital_managment_springai3.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospital_managment_springai3.entity.Doctor;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	List<Doctor> findBySpecialtyIgnoreCase(String specialty);

	List<Doctor> findByAvailableTrue();

	List<Doctor> findBySpecialtyIgnoreCaseAndAvailableTrue(String specialty);

	@Query("select distinct d.specialty from Doctor d order by d.specialty")
	List<String> findDistinctSpecialties();

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}