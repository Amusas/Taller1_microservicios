package com.uniquindio.saludonombre.dto;

// ✅ DTO de error como record
public record ErrorResponse(String timestamp, int status, String error, String message, String path) {}

