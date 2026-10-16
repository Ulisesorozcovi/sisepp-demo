package dev.demo.sisepp.catalogo.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record StrategicLine(
    UUID id,
    String name,
    String description,
    UUID pgarId,
    RecordStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
