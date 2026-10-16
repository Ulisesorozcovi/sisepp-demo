package dev.demo.sisepp.planificacion.infrastructure.output.persistence;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanningInstrumentJpaRepository extends JpaRepository<PlanningInstrumentJpaEntity, UUID> {

    List<PlanningInstrumentJpaEntity> findByType(InstrumentType type);

    List<PlanningInstrumentJpaEntity> findByTypeAndStatus(InstrumentType type, InstrumentStatus status);

    Optional<PlanningInstrumentJpaEntity> findByTypeAndStartYearAndEndYear(
        InstrumentType type, Integer startYear, Integer endYear);

    List<PlanningInstrumentJpaEntity> findByParentId(UUID parentId);
}
