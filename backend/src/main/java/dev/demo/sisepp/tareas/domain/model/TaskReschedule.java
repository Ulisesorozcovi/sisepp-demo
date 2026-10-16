package dev.demo.sisepp.tareas.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskReschedule(
    UUID id,
    UUID taskId,
    LocalDate previousDueDate,
    LocalDate newDueDate,
    String justification,
    UUID changedByUserId,
    LocalDateTime changedAt
) {}
