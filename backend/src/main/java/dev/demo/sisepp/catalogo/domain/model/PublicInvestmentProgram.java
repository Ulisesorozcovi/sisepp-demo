package dev.demo.sisepp.catalogo.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicInvestmentProgram(
    UUID id,
    String code,
    String name,
    String sector,
    String sourceUrl,
    RecordStatus status,
    LocalDateTime createdAt
) {}
