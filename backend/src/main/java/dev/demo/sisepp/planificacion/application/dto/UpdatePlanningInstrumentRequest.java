package dev.demo.sisepp.planificacion.application.dto;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdatePlanningInstrumentRequest(
    @NotBlank(message = "El nombre es obligatorio") String name,
    @NotNull(message = "El año de inicio es obligatorio") Integer startYear,
    @NotNull(message = "El año de fin es obligatorio") Integer endYear,
    LocalDate approvedDate,
    String approvalRef,
    InstrumentStatus status
) {}
