package dev.demo.sisepp.catalogo.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStrategicLineRequest(
    @NotBlank(message = "El nombre de la Línea Estratégica es obligatorio") String name,
    String description
) {}
