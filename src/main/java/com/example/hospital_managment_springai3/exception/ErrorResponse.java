package com.example.hospital_managment_springai3.exception;


import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {
	public ErrorResponse(int status, String message) {
		this(status, message, LocalDateTime.now());
	}
}