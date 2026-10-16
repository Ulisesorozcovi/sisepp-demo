package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.ManagementIndicator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementIndicatorRepositoryPort {
    List<ManagementIndicator> findActive();
    Optional<ManagementIndicator> findById(UUID id);
    ManagementIndicator save(ManagementIndicator indicator);
}
