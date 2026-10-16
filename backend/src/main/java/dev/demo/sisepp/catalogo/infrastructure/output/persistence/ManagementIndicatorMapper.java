package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.ManagementIndicator;

public final class ManagementIndicatorMapper {
    private ManagementIndicatorMapper() {}

    public static ManagementIndicator toDomain(ManagementIndicatorJpaEntity e) {
        return new ManagementIndicator(
            e.getId(), e.getCode(), e.getName(), e.getRegulationRef(),
            e.getIndicatorType(), e.getStatus(), e.getCreatedAt()
        );
    }

    public static ManagementIndicatorJpaEntity toJpa(ManagementIndicator m) {
        return ManagementIndicatorJpaEntity.builder()
            .id(m.id()).code(m.code()).name(m.name()).regulationRef(m.regulationRef())
            .indicatorType(m.indicatorType()).status(m.status()).createdAt(m.createdAt())
            .build();
    }
}
