package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityAnnualGoalJpaRepository extends JpaRepository<ActivityAnnualGoalJpaEntity, UUID> {

    List<ActivityAnnualGoalJpaEntity> findByActivityId(UUID activityId);

    List<ActivityAnnualGoalJpaEntity> findByYear(Integer year);

    Optional<ActivityAnnualGoalJpaEntity> findByActivityIdAndYear(UUID activityId, Integer year);

    void deleteByActivityId(UUID activityId);
}
