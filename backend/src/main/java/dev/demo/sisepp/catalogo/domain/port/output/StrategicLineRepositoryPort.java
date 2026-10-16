package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.StrategicLine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StrategicLineRepositoryPort {
    StrategicLine save(StrategicLine strategicLine);
    Optional<StrategicLine> findById(UUID id);
    List<StrategicLine> findAll();
    boolean existsByNameIgnoreCase(String name);
}
