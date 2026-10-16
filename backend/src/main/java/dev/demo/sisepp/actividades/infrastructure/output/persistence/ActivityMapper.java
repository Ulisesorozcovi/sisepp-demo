package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.Activity;

import java.util.Set;
import java.util.stream.Collectors;

public final class ActivityMapper {

    private ActivityMapper() {}

    public static Activity toDomain(ActivityJpaEntity entity) {
        Set<Integer> sdgIds = entity.getSdgIndicators() != null
            ? entity.getSdgIndicators().stream()
                .map(i -> i.getId())
                .collect(Collectors.toSet())
            : Set.of();

        return new Activity(
            entity.getId(),
            entity.getName(),
            entity.getProject() != null ? entity.getProject().getId() : null,
            entity.getCode(),
            entity.getMeasurementUnit(),
            entity.getGoal(),
            entity.getWeight(),
            entity.getIndicatorDescription(),
            entity.getIndicatorType(),
            entity.getThematicCategory() != null ? entity.getThematicCategory().getId() : null,
            entity.getManagementIndicator() != null ? entity.getManagementIndicator().getId() : null,
            entity.getMeasurementUnitRef() != null ? entity.getMeasurementUnitRef().getId() : null,
            entity.getStatus(),
            sdgIds,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ActivityJpaEntity toJpa(Activity domain) {
        return ActivityJpaEntity.builder()
            .id(domain.id())
            .name(domain.name())
            .code(domain.code())
            .measurementUnit(domain.measurementUnit())
            .goal(domain.goal())
            .weight(domain.weight())
            .indicatorDescription(domain.indicatorDescription())
            .indicatorType(domain.indicatorType())
            .status(domain.status())
            .build();
    }
}
