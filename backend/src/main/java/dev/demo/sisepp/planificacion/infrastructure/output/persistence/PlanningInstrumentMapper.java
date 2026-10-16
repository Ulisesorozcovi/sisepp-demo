package dev.demo.sisepp.planificacion.infrastructure.output.persistence;

import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;

public final class PlanningInstrumentMapper {

    private PlanningInstrumentMapper() {}

    public static PlanningInstrument toDomain(PlanningInstrumentJpaEntity entity) {
        return new PlanningInstrument(
            entity.getId(),
            entity.getType(),
            entity.getName(),
            entity.getStartYear(),
            entity.getEndYear(),
            entity.getParent() != null ? entity.getParent().getId() : null,
            entity.getApprovedDate(),
            entity.getApprovalRef(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static PlanningInstrumentJpaEntity toJpa(PlanningInstrument domain) {
        return PlanningInstrumentJpaEntity.builder()
            .id(domain.id())
            .type(domain.type())
            .name(domain.name())
            .startYear(domain.startYear())
            .endYear(domain.endYear())
            .approvedDate(domain.approvedDate())
            .approvalRef(domain.approvalRef())
            .status(domain.status())
            .build();
    }
}
