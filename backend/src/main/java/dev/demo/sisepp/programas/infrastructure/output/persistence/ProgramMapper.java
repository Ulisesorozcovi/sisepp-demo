package dev.demo.sisepp.programas.infrastructure.output.persistence;

import dev.demo.sisepp.programas.domain.model.Program;

public final class ProgramMapper {

    private ProgramMapper() {}

    public static Program toDomain(ProgramJpaEntity entity) {
        return new Program(
            entity.getId(),
            entity.getName(),
            entity.getCode(),
            entity.getWeight(),
            entity.getDescription(),
            entity.getStrategicLine() != null ? entity.getStrategicLine().getId() : null,
            entity.getPac() != null ? entity.getPac().getId() : null,
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ProgramJpaEntity toJpa(Program domain) {
        return ProgramJpaEntity.builder()
            .id(domain.id())
            .name(domain.name())
            .code(domain.code())
            .weight(domain.weight())
            .description(domain.description())
            .status(domain.status())
            .build();
    }
}
