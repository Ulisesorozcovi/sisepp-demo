package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.SdgIndicator;
import dev.demo.sisepp.catalogo.domain.port.output.SdgIndicatorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SdgIndicatorRepositoryAdapter implements SdgIndicatorRepositoryPort {

    private final SdgIndicatorJpaRepository jpaRepository;

    @Override
    public List<SdgIndicator> findAll() {
        return jpaRepository.findAll().stream()
            .map(SdgIndicatorMapper::toDomain)
            .sorted(Comparator.comparing(SdgIndicator::id))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<SdgIndicator> findById(Integer id) {
        return jpaRepository.findById(id).map(SdgIndicatorMapper::toDomain);
    }
}
