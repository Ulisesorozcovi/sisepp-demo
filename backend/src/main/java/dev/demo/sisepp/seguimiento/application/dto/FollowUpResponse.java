package dev.demo.sisepp.seguimiento.application.dto;

import dev.demo.sisepp.seguimiento.domain.model.FollowUp;
import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FollowUpResponse(
    UUID id,
    UUID taskId,
    /** Nombre de la tarea, para no tener que resolverlo en el cliente. */
    String taskName,
    int reportedMonth,
    int reportedYear,
    BigDecimal physicalProgress,
    String evidenceUrl,
    String attachmentUrl,
    FollowUpStatus status,
    String observations,
    UUID reportedByUserId,
    String reportedByName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static FollowUpResponse fromDomain(FollowUp f, String reportedByName) {
        return fromDomain(f, reportedByName, null);
    }

    public static FollowUpResponse fromDomain(FollowUp f, String reportedByName, String taskName) {
        return new FollowUpResponse(
            f.id(), f.taskId(), taskName, f.reportedMonth(), f.reportedYear(),
            f.physicalProgress(), f.evidenceUrl(), f.attachmentUrl(),
            f.status(), f.observations(),
            f.reportedByUserId(), reportedByName,
            f.createdAt(), f.updatedAt()
        );
    }
}
