package dev.demo.sisepp.actividades.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record Activity(
    UUID id,
    String name,
    UUID projectId,
    String code,
    String measurementUnit,
    BigDecimal goal,
    BigDecimal weight,
    String indicatorDescription,
    String indicatorType,
    UUID thematicCategoryId,
    UUID managementIndicatorId,
    UUID measurementUnitId,
    RecordStatus status,
    Set<Integer> sdgIndicatorIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
