package dev.demo.sisepp.programas.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateProgramRequest(
    @NotBlank(message = "El nombre del Programa es obligatorio") String name,
    String code,
    BigDecimal weight,
    String description
) {}
