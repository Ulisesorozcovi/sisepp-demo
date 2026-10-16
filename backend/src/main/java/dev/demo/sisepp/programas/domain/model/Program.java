package dev.demo.sisepp.programas.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Program(
    UUID id,
    String name,
    String code,
    BigDecimal weight,
    String description,
    UUID strategicLineId,
    UUID pacId,
    RecordStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
