package dev.demo.sisepp.planificacion.application.dto;

import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePlanningInstrumentRequest(
    @NotNull(message = "El tipo de instrumento es obligatorio") InstrumentType type,
    @NotBlank(message = "El nombre es obligatorio") String name,
    @NotNull(message = "El año de inicio es obligatorio") Integer startYear,
    @NotNull(message = "El año de fin es obligatorio") Integer endYear,
    UUID parentId,
    LocalDate approvedDate,
    String approvalRef
) {}
