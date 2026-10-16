package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualGoalRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityAnnualGoalRepositoryAdapter implements ActivityAnnualGoalRepositoryPort {

    private final ActivityAnnualGoalJpaRepository jpaRepository;
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityAnnualGoal save(ActivityAnnualGoal goal) {
        ActivityAnnualGoalJpaEntity entity = ActivityAnnualGoalMapper.toJpa(goal);
        if (goal.activityId() != null) {
            activityJpaRepository.findById(goal.activityId()).ifPresent(entity::setActivity);
        }
        return ActivityAnnualGoalMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<ActivityAnnualGoal> findByActivityId(UUID activityId) {
        return jpaRepository.findByActivityId(activityId).stream()
            .map(ActivityAnnualGoalMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ActivityAnnualGoal> findByYear(Integer year) {
        return jpaRepository.findByYear(year).stream()
            .map(ActivityAnnualGoalMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ActivityAnnualGoal> findByActivityIdAndYear(UUID activityId, Integer year) {
        return jpaRepository.findByActivityIdAndYear(activityId, year)
            .map(ActivityAnnualGoalMapper::toDomain);
    }

    @Override
    public List<ActivityAnnualGoal> findByActivityIds(List<UUID> activityIds) {
        return activityIds.stream()
            .flatMap(id -> jpaRepository.findByActivityId(id).stream())
            .map(ActivityAnnualGoalMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByActivityId(UUID activityId) {
        jpaRepository.deleteByActivityId(activityId);
    }
}
