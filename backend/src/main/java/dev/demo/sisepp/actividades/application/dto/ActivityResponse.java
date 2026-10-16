package dev.demo.sisepp.actividades.application.dto;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;
import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ActivityResponse(
    UUID id,
    String name,
    String code,
    String measurementUnit,
    BigDecimal goal,
    BigDecimal weight,
    String indicatorDescription,
    String indicatorType,
    UUID thematicCategoryId,
    UUID managementIndicatorId,
    UUID measurementUnitId,
    UUID projectId,
    RecordStatus status,
    Set<Integer> sdgIndicatorIds,
    List<AnnualGoalEntry> annualGoals,
    List<AnnualBudgetEntry> annualBudgets
) {
    public static ActivityResponse fromDomain(Activity activity) {
        return fromDomain(activity, List.of(), List.of());
    }

    public static ActivityResponse fromDomain(
            Activity activity,
            List<ActivityAnnualGoal> goals,
            List<ActivityAnnualBudget> budgets) {
        return new ActivityResponse(
            activity.id(),
            activity.name(),
            activity.code(),
            activity.measurementUnit(),
            activity.goal(),
            activity.weight(),
            activity.indicatorDescription(),
            activity.indicatorType(),
            activity.thematicCategoryId(),
            activity.managementIndicatorId(),
            activity.measurementUnitId(),
            activity.projectId(),
            activity.status(),
            activity.sdgIndicatorIds(),
            goals.stream()
                .map(g -> new AnnualGoalEntry(g.year(), g.goalValue()))
                .toList(),
            budgets.stream()
                .map(b -> new AnnualBudgetEntry(b.year(), b.budgetAmount()))
                .toList()
        );
    }
}
