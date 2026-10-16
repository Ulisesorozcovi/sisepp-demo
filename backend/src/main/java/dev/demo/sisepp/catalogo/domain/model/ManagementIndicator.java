package dev.demo.sisepp.catalogo.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManagementIndicator(
    UUID id,
    String code,
    String name,
    String regulationRef,
    IndicatorType indicatorType,
    RecordStatus status,
    LocalDateTime createdAt
) {}
