package dev.demo.sisepp.seguimiento.infrastructure.output.persistence;

import dev.demo.sisepp.seguimiento.domain.model.FollowUp;

public final class FollowUpMapper {

    private FollowUpMapper() {}

    public static FollowUp toDomain(FollowUpJpaEntity entity) {
        return new FollowUp(
            entity.getId(),
            entity.getTask() != null ? entity.getTask().getId() : null,
            entity.getReportedMonth(),
            entity.getReportedYear(),
            entity.getPhysicalProgress(),
            entity.getEvidenceUrl(),
            entity.getAttachmentUrl(),
            entity.getStatus(),
            entity.getObservations(),
            entity.getReportedBy() != null ? entity.getReportedBy().getId() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static FollowUpJpaEntity toJpa(FollowUp domain) {
        return FollowUpJpaEntity.builder()
            .id(domain.id())
            .reportedMonth(domain.reportedMonth())
            .reportedYear(domain.reportedYear())
            .physicalProgress(domain.physicalProgress())
            .evidenceUrl(domain.evidenceUrl())
            .attachmentUrl(domain.attachmentUrl())
            .status(domain.status())
            .observations(domain.observations())
            .build();
    }
}
