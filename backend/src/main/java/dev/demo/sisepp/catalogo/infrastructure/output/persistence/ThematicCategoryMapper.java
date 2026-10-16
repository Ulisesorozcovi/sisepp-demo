package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.ThematicCategory;

public final class ThematicCategoryMapper {
    private ThematicCategoryMapper() {}

    public static ThematicCategory toDomain(ThematicCategoryJpaEntity e) {
        return new ThematicCategory(e.getId(), e.getName(), e.getStatus(), e.getCreatedAt());
    }

    public static ThematicCategoryJpaEntity toJpa(ThematicCategory t) {
        return ThematicCategoryJpaEntity.builder()
            .id(t.id()).name(t.name()).status(t.status()).createdAt(t.createdAt())
            .build();
    }
}
