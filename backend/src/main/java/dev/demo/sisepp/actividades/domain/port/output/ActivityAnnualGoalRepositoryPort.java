package dev.demo.sisepp.actividades.domain.port.output;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityAnnualGoalRepositoryPort {
    ActivityAnnualGoal save(ActivityAnnualGoal goal);
    List<ActivityAnnualGoal> findByActivityId(UUID activityId);
    List<ActivityAnnualGoal> findByYear(Integer year);
    Optional<ActivityAnnualGoal> findByActivityIdAndYear(UUID activityId, Integer year);
    List<ActivityAnnualGoal> findByActivityIds(List<UUID> activityIds);
    void deleteByActivityId(UUID activityId);
}
