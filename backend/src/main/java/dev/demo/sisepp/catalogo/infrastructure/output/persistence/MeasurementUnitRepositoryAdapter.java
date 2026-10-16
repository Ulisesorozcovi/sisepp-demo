package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;
import dev.demo.sisepp.catalogo.domain.port.output.MeasurementUnitRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MeasurementUnitRepositoryAdapter implements MeasurementUnitRepositoryPort {

    private final MeasurementUnitJpaRepository jpa;

    @Override
    public List<MeasurementUnit> findActive() {
        return jpa.findByStatus(RecordStatus.ACTIVO).stream().map(MeasurementUnitMapper::toDomain).toList();
    }

    @Override
    public List<MeasurementUnit> findAll() {
        return jpa.findAll().stream().map(MeasurementUnitMapper::toDomain).toList();
    }

    @Override
    public Optional<MeasurementUnit> findById(UUID id) {
        return jpa.findById(id).map(MeasurementUnitMapper::toDomain);
    }

    @Override
    public MeasurementUnit save(MeasurementUnit unit) {
        MeasurementUnitJpaEntity entity;
        if (unit.id() != null) {
            entity = jpa.findById(unit.id()).orElseGet(() -> MeasurementUnitMapper.toJpa(unit));
            entity.setCode(unit.code());
            entity.setName(unit.name());
            entity.setStatus(unit.status());
        } else {
            entity = MeasurementUnitMapper.toJpa(unit);
        }
        return MeasurementUnitMapper.toDomain(jpa.save(entity));
    }
}
