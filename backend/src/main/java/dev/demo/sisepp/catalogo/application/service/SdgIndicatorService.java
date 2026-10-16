package dev.demo.sisepp.catalogo.application.service;

import dev.demo.sisepp.catalogo.application.dto.SdgIndicatorResponse;
import dev.demo.sisepp.catalogo.application.port.input.SdgIndicatorUseCase;
import dev.demo.sisepp.catalogo.domain.port.output.SdgIndicatorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SdgIndicatorService implements SdgIndicatorUseCase {

    private final SdgIndicatorRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<SdgIndicatorResponse> list() {
        return repository.findAll().stream()
            .map(SdgIndicatorResponse::fromDomain)
            .toList();
    }
}
