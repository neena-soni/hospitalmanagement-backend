package com.example.hospital_managment_springai3.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hospital_managment_springai3.dto.DoctorBookingCount;
import com.example.hospital_managment_springai3.entity.Appointment;
import com.example.hospital_managment_springai3.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	long countByDoctorIdAndAppointmentDateAndStatus(Long doctorId, LocalDate date, AppointmentStatus status);

	boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndStatus(Long patientId, Long doctorId, LocalDate date,
			AppointmentStatus status);

	//a is Appointment entity here
	// Group-by query to count booked appointments for all doctors on a specific date in one single query
	@Query("SELECT a.doctor.id as doctorId, COUNT(a.id) as count FROM Appointment a " +
	       "WHERE a.appointmentDate = :date AND a.status = :status GROUP BY a.doctor.id")
	List<DoctorBookingCount> countBookedByDateGroupedByDoctor(@Param("date") LocalDate date, @Param("status") AppointmentStatus status);

	List<Appointment> findAllByOrderByAppointmentDateDescIdDesc();

	List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);

	List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

	List<Appointment> findByDoctorIdAndAppointmentDateOrderById(Long doctorId, LocalDate date);

	// Used when Admin needs to emergency-cancel all active bookings for a doctor on a specific date
	List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(Long doctorId, LocalDate date, AppointmentStatus status);

	List<Appointment> findByAppointmentDateOrderById(LocalDate date);

	boolean existsByDoctorId(Long doctorId);

	boolean existsByPatientId(Long patientId);
}
