package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import dev.demo.sisepp.proyectos.domain.model.Project;

import java.util.Set;
import java.util.stream.Collectors;

public final class ProjectMapper {

    private ProjectMapper() {}

    public static Project toDomain(ProjectJpaEntity entity) {
        Set<java.util.UUID> coordinatorIds = entity.getCoordinatorEntries() != null
            ? entity.getCoordinatorEntries().stream()
                .map(ProjectCoordinatorJpaEntity::getUserId)
                .collect(Collectors.toSet())
            : Set.of();

        return new Project(
            entity.getId(),
            entity.getName(),
            entity.getProgram() != null ? entity.getProgram().getId() : null,
            entity.getVigencia(),
            entity.getCode(),
            entity.getSubdivision(),
            entity.getWeight(),
            entity.getObjective(),
            entity.getObjectiveWeight(),
            entity.getPip() != null ? entity.getPip().getId() : null,
            entity.getPac() != null ? entity.getPac().getId() : null,
            entity.getStartYear(),
            entity.getEndYear(),
            entity.getStatus(),
            coordinatorIds,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ProjectJpaEntity toJpa(Project domain) {
        return ProjectJpaEntity.builder()
            .id(domain.id())
            .name(domain.name())
            .vigencia(domain.vigencia())
            .code(domain.code())
            .subdivision(domain.subdivision())
            .weight(domain.weight())
            .objective(domain.objective())
            .objectiveWeight(domain.objectiveWeight())
            .startYear(domain.startYear())
            .endYear(domain.endYear())
            .status(domain.status())
            .build();
    }
}
