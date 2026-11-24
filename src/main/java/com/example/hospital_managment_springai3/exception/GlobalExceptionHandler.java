package com.example.hospital_managment_springai3.exception;



import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	// business-rule violations (doctor full, not available, bad status change...)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
		String msg = e.getBindingResult().getFieldErrors().stream()
				.map(f -> f.getField() + ": " + f.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return build(HttpStatus.BAD_REQUEST, msg);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ErrorResponse> unreadable(Exception e) {
		String detail = e.getCause() != null && e.getCause().getMessage() != null
				? e.getCause().getMessage()
				: e.getMessage();
		return build(HttpStatus.BAD_REQUEST, "Invalid request format: " + (detail != null ? detail : "check dates and enum values"));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException e) {
		String msg = "Operation violates data rules";
		String detailed = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();

		if (detailed != null) {
			String lower = detailed.toLowerCase();
			if (lower.contains("duplicate entry")) {
				// Extract the duplicate value inside quotes: "Duplicate entry 'VALUE' for key..."
				String duplicateValue = "";
				int firstQuote = detailed.indexOf('\'');
				int secondQuote = firstQuote != -1 ? detailed.indexOf('\'', firstQuote + 1) : -1;
				if (firstQuote != -1 && secondQuote != -1) {
					duplicateValue = detailed.substring(firstQuote + 1, secondQuote).trim();
				}

				// Check whether the duplicate is phone number, email, or other
				if (lower.contains("phone") || duplicateValue.matches("^[0-9]{10}$")) {
					msg = "Duplicate phone number: a record with this phone number already exists";
				} else if (lower.contains("email") || duplicateValue.contains("@")) {
					msg = "Duplicate email: a record with this email already exists";
				} else if (!duplicateValue.isEmpty()) {
					msg = "Duplicate entry '" + duplicateValue + "': a record with this value already exists";
				} else {
					msg = "Duplicate record: an entry with these details already exists";
				}
			} else if (lower.contains("foreign key") || lower.contains("cannot delete or update a parent row")) {
				msg = "Cannot delete or modify record because it is still referenced by appointments";
			}
		}

		return build(HttpStatus.CONFLICT, msg);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> generic(Exception e) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong: " + e.getMessage());
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
	}
}