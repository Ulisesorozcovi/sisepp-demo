package dev.demo.sisepp.tareas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskSchedule(
    UUID id,
    UUID taskId,
    UUID activityId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal scheduledGoal,
    String responsible,
    String observation,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
