package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualBudgetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityAnnualBudgetRepositoryAdapter implements ActivityAnnualBudgetRepositoryPort {

    private final ActivityAnnualBudgetJpaRepository jpaRepository;
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityAnnualBudget save(ActivityAnnualBudget budget) {
        ActivityAnnualBudgetJpaEntity entity = ActivityAnnualBudgetMapper.toJpa(budget);
        if (budget.activityId() != null) {
            activityJpaRepository.findById(budget.activityId()).ifPresent(entity::setActivity);
        }
        return ActivityAnnualBudgetMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<ActivityAnnualBudget> findByActivityId(UUID activityId) {
        return jpaRepository.findByActivityId(activityId).stream()
            .map(ActivityAnnualBudgetMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ActivityAnnualBudget> findByActivityIdAndYear(UUID activityId, Integer year) {
        return jpaRepository.findByActivityIdAndYear(activityId, year)
            .map(ActivityAnnualBudgetMapper::toDomain);
    }

    @Override
    public List<ActivityAnnualBudget> findByActivityIds(List<UUID> activityIds) {
        return activityIds.stream()
            .flatMap(id -> jpaRepository.findByActivityId(id).stream())
            .map(ActivityAnnualBudgetMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByActivityId(UUID activityId) {
        jpaRepository.deleteByActivityId(activityId);
    }
}
