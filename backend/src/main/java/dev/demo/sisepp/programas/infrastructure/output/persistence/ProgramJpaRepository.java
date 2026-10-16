package dev.demo.sisepp.programas.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProgramJpaRepository extends JpaRepository<ProgramJpaEntity, UUID> {
    List<ProgramJpaEntity> findByStrategicLineId(UUID strategicLineId);
    boolean existsByStrategicLineIdAndNameIgnoreCase(UUID strategicLineId, String name);

    @Query("SELECT COALESCE(SUM(p.weight), 0) FROM ProgramJpaEntity p WHERE p.strategicLine.id = :lineId AND (:excludeId IS NULL OR p.id <> :excludeId)")
    BigDecimal sumWeightsByStrategicLineId(@Param("lineId") UUID lineId, @Param("excludeId") UUID excludeId);
}
