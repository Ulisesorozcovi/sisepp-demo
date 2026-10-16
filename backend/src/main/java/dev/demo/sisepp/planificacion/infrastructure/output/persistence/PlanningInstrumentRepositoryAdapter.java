package dev.demo.sisepp.planificacion.infrastructure.output.persistence;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;
import dev.demo.sisepp.planificacion.domain.port.output.PlanningInstrumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanningInstrumentRepositoryAdapter implements PlanningInstrumentRepositoryPort {

    private final PlanningInstrumentJpaRepository jpaRepository;

    @Override
    public PlanningInstrument save(PlanningInstrument instrument) {
        PlanningInstrumentJpaEntity entity = PlanningInstrumentMapper.toJpa(instrument);
        return PlanningInstrumentMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PlanningInstrument> findById(UUID id) {
        return jpaRepository.findById(id).map(PlanningInstrumentMapper::toDomain);
    }

    @Override
    public List<PlanningInstrument> findByType(InstrumentType type) {
        return jpaRepository.findByType(type).stream()
            .map(PlanningInstrumentMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<PlanningInstrument> findByTypeAndStatus(InstrumentType type, InstrumentStatus status) {
        return jpaRepository.findByTypeAndStatus(type, status).stream()
            .map(PlanningInstrumentMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PlanningInstrument> findByTypeAndStartYearAndEndYear(InstrumentType type, Integer startYear, Integer endYear) {
        return jpaRepository.findByTypeAndStartYearAndEndYear(type, startYear, endYear)
            .map(PlanningInstrumentMapper::toDomain);
    }

    @Override
    public List<PlanningInstrument> findByParentId(UUID parentId) {
        return jpaRepository.findByParentId(parentId).stream()
            .map(PlanningInstrumentMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<PlanningInstrument> findAll() {
        return jpaRepository.findAll().stream()
            .map(PlanningInstrumentMapper::toDomain)
            .collect(Collectors.toList());
    }
}
