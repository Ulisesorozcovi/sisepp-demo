package dev.demo.sisepp.seguimiento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FollowUp(
    UUID id,
    UUID taskId,
    int reportedMonth,
    int reportedYear,
    BigDecimal physicalProgress,
    String evidenceUrl,
    String attachmentUrl,
    FollowUpStatus status,
    String observations,
    UUID reportedByUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
