package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;

public final class MeasurementUnitMapper {
    private MeasurementUnitMapper() {}

    public static MeasurementUnit toDomain(MeasurementUnitJpaEntity e) {
        return new MeasurementUnit(e.getId(), e.getCode(), e.getName(), e.getStatus(), e.getCreatedAt());
    }

    public static MeasurementUnitJpaEntity toJpa(MeasurementUnit m) {
        return MeasurementUnitJpaEntity.builder()
            .id(m.id()).code(m.code()).name(m.name()).status(m.status()).createdAt(m.createdAt())
            .build();
    }
}
