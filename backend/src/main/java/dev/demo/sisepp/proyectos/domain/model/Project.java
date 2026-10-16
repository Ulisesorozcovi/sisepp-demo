package dev.demo.sisepp.proyectos.domain.model;

import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record Project(
    UUID id,
    String name,
    UUID programId,
    int vigencia,
    String code,
    String subdivision,
    BigDecimal weight,
    String objective,
    BigDecimal objectiveWeight,
    UUID pipId,
    UUID pacId,
    Integer startYear,
    Integer endYear,
    RecordStatus status,
    Set<UUID> coordinatorIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public boolean hasCoordinator(UUID userId) {
        return coordinatorIds != null && coordinatorIds.contains(userId);
    }
}
