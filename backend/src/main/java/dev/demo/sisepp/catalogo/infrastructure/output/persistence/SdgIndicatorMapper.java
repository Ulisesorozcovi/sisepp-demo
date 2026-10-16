package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.SdgIndicator;

public final class SdgIndicatorMapper {

    private SdgIndicatorMapper() {}

    public static SdgIndicator toDomain(SdgIndicatorJpaEntity entity) {
        return new SdgIndicator(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription()
        );
    }

    public static SdgIndicatorJpaEntity toJpa(SdgIndicator domain) {
        return SdgIndicatorJpaEntity.builder()
            .id(domain.id())
            .code(domain.code())
            .name(domain.name())
            .description(domain.description())
            .build();
    }
}
