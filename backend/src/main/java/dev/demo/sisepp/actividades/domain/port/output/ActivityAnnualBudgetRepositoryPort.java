package dev.demo.sisepp.actividades.domain.port.output;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityAnnualBudgetRepositoryPort {
    ActivityAnnualBudget save(ActivityAnnualBudget budget);
    List<ActivityAnnualBudget> findByActivityId(UUID activityId);
    Optional<ActivityAnnualBudget> findByActivityIdAndYear(UUID activityId, Integer year);
    List<ActivityAnnualBudget> findByActivityIds(List<UUID> activityIds);
    void deleteByActivityId(UUID activityId);
}
