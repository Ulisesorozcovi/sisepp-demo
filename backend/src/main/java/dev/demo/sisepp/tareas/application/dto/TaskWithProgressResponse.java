package dev.demo.sisepp.tareas.application.dto;

import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskWithProgressResponse(
    UUID id,
    String name,
    String responsibleName,
    BigDecimal physicalGoalValue,
    String physicalGoalUnit,
    BigDecimal executedAccumulated,
    BigDecimal completionPercent,
    FollowUpStatus lastFollowUpStatus,
    LocalDateTime lastFollowUpDate,
    LocalDate startDate,
    LocalDate dueDate,
    TaskStatus status,
    UUID activityId,
    String activityName
) {}
