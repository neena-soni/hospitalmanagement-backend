package com.example.hospital_managment_springai3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital_managment_springai3.dto.AppointmentRequest;
import com.example.hospital_managment_springai3.dto.AppointmentResponse;
import com.example.hospital_managment_springai3.dto.CapacityResponse;
import com.example.hospital_managment_springai3.dto.DoctorBookingCount;
import com.example.hospital_managment_springai3.entity.Appointment;
import com.example.hospital_managment_springai3.entity.AppointmentStatus;
import com.example.hospital_managment_springai3.entity.Doctor;
import com.example.hospital_managment_springai3.entity.Patient;
import com.example.hospital_managment_springai3.exception.ResourceNotFoundException;
import com.example.hospital_managment_springai3.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

	public static final int MAX_PATIENTS_PER_DAY = 10;

	private final AppointmentRepository repo;
	private final DoctorService doctorService;
	private final PatientService patientService;

	@Transactional
	public AppointmentResponse book(AppointmentRequest r) {
		Patient patient = patientService.getPatient(r.patientId());
		Doctor doctor = doctorService.getEntity(r.doctorId());
		LocalDate date = r.appointmentDate();

		String problem = doctorProblem(doctor, date);
		if (problem != null) throw new IllegalArgumentException(problem);

		if (countBooked(doctor.getId(), date) >= MAX_PATIENTS_PER_DAY) {
			throw new IllegalArgumentException(
					"Dr. " + doctor.getName() + " is fully booked on " + date + " (max " + MAX_PATIENTS_PER_DAY + " patients)");
		}

		// Smarter check: only prevent active (BOOKED) duplicate appointments with the same doctor on the same date
		if (repo.existsByPatientIdAndDoctorIdAndAppointmentDateAndStatus(
				patient.getId(), doctor.getId(), date, AppointmentStatus.BOOKED)) {
			throw new IllegalArgumentException("Patient already has an active booked appointment with Dr. " + doctor.getName() + " on " + date);
		}

		Appointment a = Appointment.builder()
				.patient(patient)
				.doctor(doctor)
				.appointmentDate(date)
				.reason(r.reason())
				.status(AppointmentStatus.BOOKED)
				.createdAt(LocalDateTime.now())
				.build();
		return toResponse(repo.save(a));
	}

	@Transactional
	public AppointmentResponse cancel(Long id) {
		Appointment a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
		requireBooked(a, "cancelled");
		a.setStatus(AppointmentStatus.CANCELLED);
		return toResponse(repo.save(a));
	}

	// Emergency Doctor Cancel (Admin only ).
	// When a doctor takes emergency leave on a date where appointments are booked,
	// this marks all active bookings as CLINIC_CANCELLED in one go.
	@Transactional
	public List<AppointmentResponse> cancelDoctorDaySchedule(Long doctorId, LocalDate date) {
		List<Appointment> bookings = repo.findByDoctorIdAndAppointmentDateAndStatus(
				doctorId, date, AppointmentStatus.BOOKED);

		for (Appointment a : bookings) {
			a.setStatus(AppointmentStatus.CLINIC_CANCELLED);
	
		}

		return repo.saveAll(bookings).stream().map(this::toResponse).toList();
	}

	@Transactional
	public AppointmentResponse markNoShow(Long id) {
		Appointment a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
		requireBooked(a, "marked as no-show");
		if (a.getAppointmentDate().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Cannot mark no-show for a future appointment");
		}
		a.setStatus(AppointmentStatus.NO_SHOW);
		return toResponse(repo.save(a));
	}

	//to check if booked or not to cancel or no show.
	private void requireBooked(Appointment a, String action) {
		if (a.getStatus() != AppointmentStatus.BOOKED) {
			throw new IllegalArgumentException(
					"Only BOOKED appointments can be " + action + " (current status: " + a.getStatus() + ")");
		}
	}

	// Calculates capacity for a single doctor on a date
	public CapacityResponse getCapacity(Long doctorId, LocalDate date) {
		Doctor d = doctorService.getEntity(doctorId);
		long booked = countBooked(doctorId, date);
		return calculateCapacityResponse(d, date, booked);
	}

	// BATCH CAPACITY ENDPOINT: Calculates capacity for ALL doctors on a date in a single SQL query
	public Map<Long, CapacityResponse> getBatchCapacity(LocalDate date) {
		List<Doctor> allDoctors = doctorService.searchEntities(null, false);

		// Group-by counts in one single database query
		List<DoctorBookingCount> bookingCounts = repo.countBookedByDateGroupedByDoctor(date, AppointmentStatus.BOOKED);
		Map<Long, Long> countMap = new HashMap<>();
		for (DoctorBookingCount bc : bookingCounts) {
			countMap.put(bc.getDoctorId(), bc.getCount());
		}

		Map<Long, CapacityResponse> resultMap = new HashMap<>();
		for (Doctor doc : allDoctors) {
			long booked = countMap.getOrDefault(doc.getId(), 0L);
			resultMap.put(doc.getId(), calculateCapacityResponse(doc, date, booked));
		}
		return resultMap;
	}

	// Shared helper to build CapacityResponse
	private CapacityResponse calculateCapacityResponse(Doctor d, LocalDate date, long booked) {
		long remaining = Math.max(0, MAX_PATIENTS_PER_DAY - booked);
		String problem = doctorProblem(d, date);

		boolean canBook = problem == null && remaining > 0;
		String message = problem != null ? problem
				: remaining == 0 ? "Fully booked for this date"
				: remaining + " of " + MAX_PATIENTS_PER_DAY + " spots left";

		return new CapacityResponse(d.getId(), d.getName(), d.getSpecialty(), date,
				MAX_PATIENTS_PER_DAY, booked, remaining, canBook, message);
	}

	public AppointmentResponse getById(Long id) {
		return toResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id)));
	}

	public List<AppointmentResponse> getAll() {
		List<Appointment> list = repo.findAllByOrderByAppointmentDateDescIdDesc();
		return list.stream().map(this::toResponse).toList();
	}

	public List<AppointmentResponse> getByPatient(Long patientId) {
		List<Appointment> list = repo.findByPatientIdOrderByAppointmentDateDesc(patientId);
		return list.stream().map(this::toResponse).toList();
	}

	public List<AppointmentResponse> getByDoctor(Long doctorId) {
		List<Appointment> list = repo.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
		return list.stream().map(this::toResponse).toList();		
	}

	public List<AppointmentResponse> getByDate(LocalDate date) {
		List<Appointment> list = repo.findByAppointmentDateOrderById(date);
		return list.stream().map(this::toResponse).toList();
	}

	public List<AppointmentResponse> getByDoctorAndDate(Long doctorId, LocalDate date) {
		List<Appointment> list = repo.findByDoctorIdAndAppointmentDateOrderById(doctorId, date);
		return list.stream().map(this::toResponse).toList();
	}

	private long countBooked(Long doctorId, LocalDate date) {
		return repo.countByDoctorIdAndAppointmentDateAndStatus(doctorId, date, AppointmentStatus.BOOKED);
	}

	private String doctorProblem(Doctor d, LocalDate date) {
		if (!d.isAvailable()) return "Doctor is currently not available";
		if (date.isBefore(LocalDate.now())) return "Date is in the past";
		if (date.isAfter(LocalDate.now().plusMonths(3))) return "Appointments can only be booked up to 3 months in advance";
		if (!d.getAvailableDays().isEmpty() && !d.getAvailableDays().contains(date.getDayOfWeek())) {
			return "Doctor does not work on " + date.getDayOfWeek();
		}
		return null;
	}

	public AppointmentResponse toResponse(Appointment a) {
		return AppointmentResponse.from(a);
	}
}
