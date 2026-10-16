package dev.demo.sisepp.actividades.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityAnnualGoal(
    UUID id,
    UUID activityId,
    int year,
    BigDecimal goalValue,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
