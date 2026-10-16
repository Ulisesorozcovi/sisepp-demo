package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.ThematicCategory;
import dev.demo.sisepp.catalogo.domain.port.output.ThematicCategoryRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ThematicCategoryRepositoryAdapter implements ThematicCategoryRepositoryPort {

    private final ThematicCategoryJpaRepository jpa;

    @Override
    public List<ThematicCategory> findActive() {
        return jpa.findByStatus(RecordStatus.ACTIVO).stream().map(ThematicCategoryMapper::toDomain).toList();
    }

    @Override
    public Optional<ThematicCategory> findById(UUID id) {
        return jpa.findById(id).map(ThematicCategoryMapper::toDomain);
    }

    @Override
    public ThematicCategory save(ThematicCategory category) {
        ThematicCategoryJpaEntity entity;
        if (category.id() != null) {
            entity = jpa.findById(category.id()).orElseGet(() -> ThematicCategoryMapper.toJpa(category));
            entity.setName(category.name());
            entity.setStatus(category.status());
        } else {
            entity = ThematicCategoryMapper.toJpa(category);
        }
        return ThematicCategoryMapper.toDomain(jpa.save(entity));
    }
}
