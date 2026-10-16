package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.ThematicCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThematicCategoryRepositoryPort {
    List<ThematicCategory> findActive();
    Optional<ThematicCategory> findById(UUID id);
    ThematicCategory save(ThematicCategory category);
}
