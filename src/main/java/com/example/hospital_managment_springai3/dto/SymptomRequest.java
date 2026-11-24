package com.example.hospital_managment_springai3.dto;

import jakarta.validation.constraints.NotBlank;

public record SymptomRequest(@NotBlank(message = "Please describe the symptoms") String symptoms) {
}