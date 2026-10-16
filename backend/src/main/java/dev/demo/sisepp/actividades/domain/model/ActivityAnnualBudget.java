package dev.demo.sisepp.actividades.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityAnnualBudget(
    UUID id,
    UUID activityId,
    int year,
    BigDecimal budgetAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
