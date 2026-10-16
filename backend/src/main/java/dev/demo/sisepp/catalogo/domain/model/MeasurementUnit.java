package dev.demo.sisepp.catalogo.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeasurementUnit(
    UUID id,
    int code,
    String name,
    RecordStatus status,
    LocalDateTime createdAt
) {}
