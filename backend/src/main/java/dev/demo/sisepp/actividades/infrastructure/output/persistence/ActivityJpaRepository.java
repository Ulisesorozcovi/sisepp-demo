package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ActivityJpaRepository extends JpaRepository<ActivityJpaEntity, UUID> {
    List<ActivityJpaEntity> findByProjectId(UUID projectId);
    List<ActivityJpaEntity> findByProject_IdIn(List<UUID> projectIds);

    @Query("SELECT COALESCE(SUM(a.weight), 0) FROM ActivityJpaEntity a WHERE a.project.id = :projectId AND (:excludeId IS NULL OR a.id <> :excludeId)")
    BigDecimal sumWeightsByProjectId(@Param("projectId") UUID projectId, @Param("excludeId") UUID excludeId);
}
