package dev.demo.sisepp.proyectos.application.dto;

import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String name,
    String code,
    String subdivision,
    BigDecimal weight,
    String objective,
    BigDecimal objectiveWeight,
    UUID pipId,
    UUID programId,
    Integer vigencia,
    RecordStatus status,
    Set<UUID> coordinatorIds
) {
    public static ProjectResponse fromDomain(Project project) {
        return new ProjectResponse(
            project.id(),
            project.name(),
            project.code(),
            project.subdivision(),
            project.weight(),
            project.objective(),
            project.objectiveWeight(),
            project.pipId(),
            project.programId(),
            project.vigencia(),
            project.status(),
            project.coordinatorIds()
        );
    }
}
