package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityAnnualBudgetJpaRepository extends JpaRepository<ActivityAnnualBudgetJpaEntity, UUID> {

    List<ActivityAnnualBudgetJpaEntity> findByActivityId(UUID activityId);

    Optional<ActivityAnnualBudgetJpaEntity> findByActivityIdAndYear(UUID activityId, Integer year);

    void deleteByActivityId(UUID activityId);
}
