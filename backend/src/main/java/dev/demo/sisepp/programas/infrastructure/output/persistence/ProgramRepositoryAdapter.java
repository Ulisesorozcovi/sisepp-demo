package dev.demo.sisepp.programas.infrastructure.output.persistence;

import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.programas.domain.port.output.ProgramRepositoryPort;
import dev.demo.sisepp.catalogo.infrastructure.output.persistence.StrategicLineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProgramRepositoryAdapter implements ProgramRepositoryPort {

    private final ProgramJpaRepository jpaRepository;
    private final StrategicLineJpaRepository strategicLineJpaRepository;

    @Override
    public Program save(Program program) {
        ProgramJpaEntity entity = ProgramMapper.toJpa(program);
        if (program.strategicLineId() != null) {
            strategicLineJpaRepository.findById(program.strategicLineId())
                .ifPresent(entity::setStrategicLine);
        }
        return ProgramMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Program> findById(UUID id) {
        return jpaRepository.findById(id).map(ProgramMapper::toDomain);
    }

    @Override
    public List<Program> findByStrategicLineId(UUID strategicLineId) {
        return jpaRepository.findByStrategicLineId(strategicLineId).stream()
            .map(ProgramMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStrategicLineIdAndNameIgnoreCase(UUID strategicLineId, String name) {
        return jpaRepository.existsByStrategicLineIdAndNameIgnoreCase(strategicLineId, name);
    }

    @Override
    public List<Program> findAll() {
        return jpaRepository.findAll().stream()
            .map(ProgramMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumWeightsByStrategicLineId(UUID strategicLineId, UUID excludeId) {
        return jpaRepository.sumWeightsByStrategicLineId(strategicLineId, excludeId);
    }
}
