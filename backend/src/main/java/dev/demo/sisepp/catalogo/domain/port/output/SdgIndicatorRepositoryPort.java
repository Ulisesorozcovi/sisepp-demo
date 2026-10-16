package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.SdgIndicator;

import java.util.List;
import java.util.Optional;

public interface SdgIndicatorRepositoryPort {
    List<SdgIndicator> findAll();
    Optional<SdgIndicator> findById(Integer id);
}
