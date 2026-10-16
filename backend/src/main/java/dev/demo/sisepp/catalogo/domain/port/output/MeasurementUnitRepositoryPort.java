package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasurementUnitRepositoryPort {
    List<MeasurementUnit> findActive();
    List<MeasurementUnit> findAll();
    Optional<MeasurementUnit> findById(UUID id);
    MeasurementUnit save(MeasurementUnit unit);
}
