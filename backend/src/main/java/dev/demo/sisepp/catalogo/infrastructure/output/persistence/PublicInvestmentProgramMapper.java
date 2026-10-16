package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.PublicInvestmentProgram;

public final class PublicInvestmentProgramMapper {
    private PublicInvestmentProgramMapper() {}

    public static PublicInvestmentProgram toDomain(PublicInvestmentProgramJpaEntity e) {
        return new PublicInvestmentProgram(
            e.getId(), e.getCode(), e.getName(), e.getSector(), e.getSourceUrl(), e.getStatus(), e.getCreatedAt()
        );
    }

    public static PublicInvestmentProgramJpaEntity toJpa(PublicInvestmentProgram p) {
        return PublicInvestmentProgramJpaEntity.builder()
            .id(p.id()).code(p.code()).name(p.name()).sector(p.sector())
            .sourceUrl(p.sourceUrl()).status(p.status()).createdAt(p.createdAt())
            .build();
    }
}
