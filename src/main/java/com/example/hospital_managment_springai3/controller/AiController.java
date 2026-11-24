package com.example.hospital_managment_springai3.controller;



import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital_managment_springai3.dto.AskRequest;
import com.example.hospital_managment_springai3.dto.AskResponse;
import com.example.hospital_managment_springai3.dto.SpecialtySuggestion;
import com.example.hospital_managment_springai3.dto.SymptomRequest;
import com.example.hospital_managment_springai3.service.DoctorService;
import com.example.hospital_managment_springai3.tools.HospitalReadTools;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	private static final String ASSISTANT_PROMPT = """
			You are the assistant of a Hospital Management System. Today's date is %s (%s).
			- Answer only about doctors, patients' appointments, specialties and doctor availability in this system.
			- ALWAYS use the tools to get data. Never invent doctors, patients, dates or appointments.
			- Each doctor can take at most 10 patients per day. Use the capacity tools to check free spots.
			- Convert relative dates like 'tomorrow' or 'next Monday' to yyyy-MM-dd using today's date.
			- You can only READ data. If asked to book, cancel or change anything, politely say it must be done
			  from the Appointments page of the application. You may tell the user which doctor and date to pick.
			- You are not a doctor: never diagnose or give medical advice.
			- If the question is unrelated to this hospital system, politely decline.
			""";

	private static final String SYMPTOM_PROMPT = """
			You help route patients to a medical specialty. You do NOT diagnose.
			Current hospital departments and specialities available are: %s.
			If the symptoms match one of the available hospital specialties, pick that exact specialty.
			If the symptoms require a specialty NOT available in the hospital (e.g. skin, eye, dental etc.):
       - Set specialty to "General Medicine" (or the closest available specialty).
       - In the reason, clearly explain that while the symptoms usually require [Specialist Name], the hospital does not currently have that department, and advise seeing General Medicine or an external specialist.
			Set urgent=true if the symptoms could be an emergency (e.g. severe chest pain, trouble breathing,
			stroke signs, heavy bleeding etc.); then the reason must advise going to emergency services immediately, but don't give
			any message that creates panic to the patient or user.
			
			You MUST always respond with valid JSON matching the schema. Never return empty content or plain text.
			
			CRITICAL RULE FOR OFF-TOPIC INPUTS:
			If the message is unrelated to medical symptoms or patient complaints, you must STILL return a valid JSON object where:
			- specialty is set to "General"
			- urgent is false
			- reason is set to: "I am here to help route patients to a proper specialty and cannot answer beyond my scope of just being a medical system assistant."
			""";

	private final ChatClient chatClient;
	private final HospitalReadTools readTools;
	private final DoctorService doctorService;

	public AiController(ChatClient.Builder builder, HospitalReadTools readTools, DoctorService doctorService) {
		this.chatClient = builder.build();
		this.readTools = readTools;
		this.doctorService = doctorService;
	}

	
	
	@GetMapping("/ask")
	public AskResponse askGet(@RequestParam String message) {
		return processAsk(message);
	}

	@GetMapping("/suggest-specialty")
	public SpecialtySuggestion suggestSpecialtyGet(@RequestParam String symptoms) {
		return processSpecialty(symptoms);
	}

	// --- Shared Helper Logic to Avoid Code Duplication ---

	private AskResponse processAsk(String message) {
		LocalDate today = LocalDate.now();
		String system = ASSISTANT_PROMPT.formatted(today, today.getDayOfWeek());
		String answer = chatClient.prompt()
				.system(system)
				.user(message)
				.tools(readTools)
				.call()
				.content();
		return new AskResponse(answer);
	}

	private SpecialtySuggestion processSpecialty(String symptoms) {
		String specialties = String.join(", ", doctorService.getSpecialties());
		return chatClient.prompt()
				.system(SYMPTOM_PROMPT.formatted(specialties))
				.user(symptoms)
				.call()
				.entity(SpecialtySuggestion.class);   //to map response into a properly formatted SpecialitySuggestion class object.
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@PostMapping("/ask")
	public AskResponse ask(@Valid @RequestBody AskRequest request) {
		LocalDate today = LocalDate.now();
		String system = ASSISTANT_PROMPT.formatted(today, today.getDayOfWeek());  //replaces %s in the prompt with today's date
		String answer = chatClient.prompt()
				.system(system)
				.user(request.message())
				.tools(readTools) // read-only tools only
				.call()
				.content();
		return new AskResponse(answer);
	}

	@PostMapping("/suggest-specialty")
	public SpecialtySuggestion suggestSpecialty(@Valid @RequestBody SymptomRequest request) {
		String specialties = String.join(", ", doctorService.getSpecialties());
		return chatClient.prompt()
				.system(SYMPTOM_PROMPT.formatted(specialties))   //replaces %s in prompt with all the current specialities in database
				.user(request.symptoms())
				.call()
				.entity(SpecialtySuggestion.class);  //asks spring ai to answer in json by matching  fields of SpecialitySuggesion
	}
	
	
	
	
	
	
	//for testing directly from browser:-
	
	

}


//http://localhost:8080/api/ai/ask?message=What doctors are available today?