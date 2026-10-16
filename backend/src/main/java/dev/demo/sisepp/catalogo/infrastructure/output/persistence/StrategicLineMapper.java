package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.StrategicLine;

public final class StrategicLineMapper {

    private StrategicLineMapper() {}

    public static StrategicLine toDomain(StrategicLineJpaEntity entity) {
        return new StrategicLine(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPgar() != null ? entity.getPgar().getId() : null,
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static StrategicLineJpaEntity toJpa(StrategicLine domain) {
        return StrategicLineJpaEntity.builder()
            .id(domain.id())
            .name(domain.name())
            .description(domain.description())
            .status(domain.status())
            .build();
    }
}
