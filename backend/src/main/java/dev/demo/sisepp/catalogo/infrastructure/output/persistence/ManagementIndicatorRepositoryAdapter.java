package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.ManagementIndicator;
import dev.demo.sisepp.catalogo.domain.port.output.ManagementIndicatorRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ManagementIndicatorRepositoryAdapter implements ManagementIndicatorRepositoryPort {

    private final ManagementIndicatorJpaRepository jpa;

    @Override
    public List<ManagementIndicator> findActive() {
        return jpa.findByStatus(RecordStatus.ACTIVO).stream().map(ManagementIndicatorMapper::toDomain).toList();
    }

    @Override
    public Optional<ManagementIndicator> findById(UUID id) {
        return jpa.findById(id).map(ManagementIndicatorMapper::toDomain);
    }

    @Override
    public ManagementIndicator save(ManagementIndicator indicator) {
        ManagementIndicatorJpaEntity entity;
        if (indicator.id() != null) {
            entity = jpa.findById(indicator.id()).orElseGet(() -> ManagementIndicatorMapper.toJpa(indicator));
            entity.setCode(indicator.code());
            entity.setName(indicator.name());
            entity.setRegulationRef(indicator.regulationRef());
            entity.setIndicatorType(indicator.indicatorType());
            entity.setStatus(indicator.status());
        } else {
            entity = ManagementIndicatorMapper.toJpa(indicator);
        }
        return ManagementIndicatorMapper.toDomain(jpa.save(entity));
    }
}
