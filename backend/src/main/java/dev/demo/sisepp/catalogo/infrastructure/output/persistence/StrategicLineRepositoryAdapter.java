package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.StrategicLine;
import dev.demo.sisepp.catalogo.domain.port.output.StrategicLineRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StrategicLineRepositoryAdapter implements StrategicLineRepositoryPort {

    private final StrategicLineJpaRepository jpaRepository;

    @Override
    public StrategicLine save(StrategicLine strategicLine) {
        StrategicLineJpaEntity entity = StrategicLineMapper.toJpa(strategicLine);
        return StrategicLineMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<StrategicLine> findById(UUID id) {
        return jpaRepository.findById(id).map(StrategicLineMapper::toDomain);
    }

    @Override
    public List<StrategicLine> findAll() {
        return jpaRepository.findAll().stream()
            .map(StrategicLineMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}
