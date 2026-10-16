package dev.demo.sisepp.proyectos.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProjectRequest(
    @NotBlank(message = "El nombre del Proyecto es obligatorio") String name,
    @NotNull(message = "Debe asociar el Proyecto a un Programa") UUID programId,
    @NotNull(message = "La vigencia es obligatoria") @Min(value = 2000, message = "Vigencia inválida") Integer vigencia,
    String code,
    String subdivision,
    BigDecimal weight,
    String objective,
    BigDecimal objectiveWeight,
    UUID pipId
) {}
