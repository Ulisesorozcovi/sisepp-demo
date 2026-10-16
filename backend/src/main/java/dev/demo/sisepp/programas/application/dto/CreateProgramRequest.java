package dev.demo.sisepp.programas.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProgramRequest(
    @NotBlank(message = "El nombre del Programa es obligatorio") String name,
    @NotNull(message = "Debe asociar el Programa a una Línea Estratégica") UUID strategicLineId,
    String code,
    BigDecimal weight,
    String description
) {}
