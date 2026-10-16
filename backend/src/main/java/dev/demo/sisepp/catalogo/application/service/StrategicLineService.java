package dev.demo.sisepp.catalogo.application.service;

import dev.demo.sisepp.catalogo.application.dto.CreateStrategicLineRequest;
import dev.demo.sisepp.catalogo.application.dto.StrategicLineResponse;
import dev.demo.sisepp.catalogo.application.dto.UpdateStrategicLineRequest;
import dev.demo.sisepp.catalogo.domain.model.StrategicLine;
import dev.demo.sisepp.catalogo.application.port.input.StrategicLineUseCase;
import dev.demo.sisepp.catalogo.domain.port.output.StrategicLineRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.DuplicateNameException;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StrategicLineService implements StrategicLineUseCase {

    private final StrategicLineRepositoryPort repository;

    @Override
    @Transactional
    public StrategicLineResponse create(CreateStrategicLineRequest request) {
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateNameException("Ya existe una Línea Estratégica con ese nombre");
        }
        StrategicLine line = new StrategicLine(
            null, request.name(), request.description(), null,
            RecordStatus.ACTIVO, null, null
        );
        return StrategicLineResponse.fromDomain(repository.save(line));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrategicLineResponse> list() {
        return repository.findAll().stream().map(StrategicLineResponse::fromDomain).toList();
    }

    @Override
    @Transactional
    public StrategicLineResponse update(UUID id, UpdateStrategicLineRequest request) {
        StrategicLine line = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Línea Estratégica no encontrada"));
        StrategicLine updated = new StrategicLine(
            line.id(), request.name(), request.description(), line.pgarId(),
            line.status(), line.createdAt(), line.updatedAt()
        );
        return StrategicLineResponse.fromDomain(repository.save(updated));
    }
}
