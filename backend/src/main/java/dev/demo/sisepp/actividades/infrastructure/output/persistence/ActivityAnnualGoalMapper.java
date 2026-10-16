package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;

public final class ActivityAnnualGoalMapper {

    private ActivityAnnualGoalMapper() {}

    public static ActivityAnnualGoal toDomain(ActivityAnnualGoalJpaEntity entity) {
        return new ActivityAnnualGoal(
            entity.getId(),
            entity.getActivity() != null ? entity.getActivity().getId() : null,
            entity.getYear(),
            entity.getGoalValue(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ActivityAnnualGoalJpaEntity toJpa(ActivityAnnualGoal domain) {
        return ActivityAnnualGoalJpaEntity.builder()
            .id(domain.id())
            .year(domain.year())
            .goalValue(domain.goalValue())
            .build();
    }
}
