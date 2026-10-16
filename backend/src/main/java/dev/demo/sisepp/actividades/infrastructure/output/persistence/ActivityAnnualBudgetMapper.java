package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;

public final class ActivityAnnualBudgetMapper {

    private ActivityAnnualBudgetMapper() {}

    public static ActivityAnnualBudget toDomain(ActivityAnnualBudgetJpaEntity entity) {
        return new ActivityAnnualBudget(
            entity.getId(),
            entity.getActivity() != null ? entity.getActivity().getId() : null,
            entity.getYear(),
            entity.getBudgetAmount(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ActivityAnnualBudgetJpaEntity toJpa(ActivityAnnualBudget domain) {
        return ActivityAnnualBudgetJpaEntity.builder()
            .id(domain.id())
            .year(domain.year())
            .budgetAmount(domain.budgetAmount())
            .build();
    }
}
