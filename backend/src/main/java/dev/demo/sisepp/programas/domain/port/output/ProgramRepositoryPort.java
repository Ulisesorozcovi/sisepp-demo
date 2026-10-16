package dev.demo.sisepp.programas.domain.port.output;

import dev.demo.sisepp.programas.domain.model.Program;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgramRepositoryPort {
    Program save(Program program);
    Optional<Program> findById(UUID id);
    List<Program> findByStrategicLineId(UUID strategicLineId);
    boolean existsByStrategicLineIdAndNameIgnoreCase(UUID strategicLineId, String name);
    List<Program> findAll();
    BigDecimal sumWeightsByStrategicLineId(UUID strategicLineId, UUID excludeId);
}
