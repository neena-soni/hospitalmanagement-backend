package com.example.hospital_managment_springai3.tools;

import java.time.LocalDate;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.hospital_managment_springai3.dto.AppointmentResponse;
import com.example.hospital_managment_springai3.dto.CapacityResponse;
import com.example.hospital_managment_springai3.dto.DoctorInfo;
import com.example.hospital_managment_springai3.service.AppointmentService;
import com.example.hospital_managment_springai3.service.DoctorService;

import lombok.RequiredArgsConstructor;

/**
 * READ-ONLY tools exposed to the AI assistant. Everything in this class is visible to the model,
 * so never add create/update/delete/book/cancel methods here.
 */
@Component
@RequiredArgsConstructor
public class HospitalReadTools {

	private final DoctorService doctorService;
	private final AppointmentService appointmentService;

	@Tool(description = "List doctors with all their details: specialty, working days (empty = works every day), "
			+ "shift times, fee, and how many appointment spots are booked/left on a date (max 10 per day). "
			+ "Optionally filter by specialty. If date is omitted, today's date is used. Date format yyyy-MM-dd. "
			+ "canBook=false means the doctor cannot take appointments that day (see note).")
	public List<DoctorInfo> listDoctors(@ToolParam(description = "Specialty name", required = false) String specialty,
			@ToolParam(description = "Date in yyyy-MM-dd; defaults to today", required = false) String date) {
		LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
		return doctorService.searchEntities(specialty, false).stream().map(doc -> {
			CapacityResponse c = appointmentService.getCapacity(doc.getId(), d);
			return new DoctorInfo(doc.getId(), doc.getName(), doc.getSpecialty(), doc.getAvailableDays(),
					doc.getShiftStartTime(), doc.getShiftEndTime(), doc.getConsultationFee(),
					d, c.booked(), c.remaining(), c.canBook(), c.message());
		}).toList();
	}

	@Tool(description = "List the medical specialties available in this hospital")
	public List<String> listSpecialties() {
		return doctorService.getSpecialties();
	}

	@Tool(description = "List appointments of a doctor, optionally only for one date (yyyy-MM-dd). "
			+ "Each appointment shows its status: BOOKED, CANCELLED or NO_SHOW.")
	public List<AppointmentResponse> listDoctorAppointments(
			@ToolParam(description = "Doctor id") Long doctorId,
			@ToolParam(description = "Date in yyyy-MM-dd", required = false) String date) {
		if (date == null || date.isBlank()) return appointmentService.getByDoctor(doctorId);
		return appointmentService.getByDoctorAndDate(doctorId, LocalDate.parse(date));
	}

	//this can be a problem when another patient tries to see this.
	@Tool(description = "List all appointments of a patient, with status (BOOKED, CANCELLED, NO_SHOW)")
	public List<AppointmentResponse> listPatientAppointments(@ToolParam(description = "Patient id") Long patientId) {
		return appointmentService.getByPatient(patientId);
	}

	@Tool(description = "List all appointments on a given date (yyyy-MM-dd), with status")
	public List<AppointmentResponse> listAppointmentsOnDate(@ToolParam(description = "Date in yyyy-MM-dd") String date) {
		return appointmentService.getByDate(LocalDate.parse(date));
	}
}
