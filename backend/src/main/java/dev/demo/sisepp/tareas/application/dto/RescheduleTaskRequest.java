package dev.demo.sisepp.tareas.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RescheduleTaskRequest(
    @NotNull(message = "La nueva fecha límite es obligatoria") @Future(message = "La nueva fecha debe ser posterior a hoy") LocalDate newDueDate,
    @NotBlank(message = "Debe justificar la reprogramación")
    @Size(min = 10, message = "La justificación debe tener al menos 10 caracteres") String justification
) {}
