package dev.demo.sisepp.tareas.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
    @NotNull(message = "Debe asociar la tarea a una Actividad") UUID activityId,
    @NotBlank(message = "El nombre de la tarea es obligatorio") String name,
    @NotNull(message = "La meta física es obligatoria") @Positive(message = "La meta física debe ser mayor a cero") BigDecimal physicalGoalValue,
    @NotBlank(message = "La unidad de la meta física es obligatoria") String physicalGoalUnit,
    @NotNull(message = "La fecha límite es obligatoria") @Future(message = "La fecha límite debe ser posterior a hoy") LocalDate dueDate,
    @NotBlank(message = "El responsable es obligatorio") String responsibleName,
    @NotNull(message = "Debe seleccionar un usuario responsable") UUID responsibleUserId,
    String taskType,
    BigDecimal weight,
    String observation,
    LocalDate startDate
) {}
