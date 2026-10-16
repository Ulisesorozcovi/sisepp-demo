package dev.demo.sisepp.catalogo.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateMeasurementUnitRequest(
    @Positive(message = "El código debe ser positivo") int code,
    @NotBlank(message = "El nombre es obligatorio") String name
) {}
