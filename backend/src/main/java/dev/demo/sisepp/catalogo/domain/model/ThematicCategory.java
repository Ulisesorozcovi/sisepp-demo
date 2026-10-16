package dev.demo.sisepp.catalogo.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ThematicCategory(
    UUID id,
    String name,
    RecordStatus status,
    LocalDateTime createdAt
) {}
