package dev.demo.sisepp.auth.application.dto;

public record LoginResponse(
    String token,
    String fullName,
    String role,
    String email
) {}
