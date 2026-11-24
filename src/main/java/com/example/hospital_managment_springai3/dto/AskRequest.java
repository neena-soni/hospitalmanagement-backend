package com.example.hospital_managment_springai3.dto;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(@NotBlank(message = "Message is required") String message) {
}