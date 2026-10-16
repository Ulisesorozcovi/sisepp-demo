package dev.demo.sisepp.planificacion.domain.port.output;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanningInstrumentRepositoryPort {
    PlanningInstrument save(PlanningInstrument instrument);
    Optional<PlanningInstrument> findById(UUID id);
    List<PlanningInstrument> findByType(InstrumentType type);
    List<PlanningInstrument> findByTypeAndStatus(InstrumentType type, InstrumentStatus status);
    Optional<PlanningInstrument> findByTypeAndStartYearAndEndYear(InstrumentType type, Integer startYear, Integer endYear);
    List<PlanningInstrument> findByParentId(UUID parentId);
    List<PlanningInstrument> findAll();
}
