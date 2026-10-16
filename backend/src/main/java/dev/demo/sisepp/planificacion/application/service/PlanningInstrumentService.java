package dev.demo.sisepp.planificacion.application.service;

import dev.demo.sisepp.planificacion.application.dto.CreatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.application.dto.PlanningInstrumentResponse;
import dev.demo.sisepp.planificacion.application.dto.UpdatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;
import dev.demo.sisepp.planificacion.application.port.input.PlanningInstrumentUseCase;
import dev.demo.sisepp.planificacion.domain.port.output.PlanningInstrumentRepositoryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanningInstrumentService implements PlanningInstrumentUseCase {

    private final PlanningInstrumentRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<PlanningInstrumentResponse> list(InstrumentType type, InstrumentStatus status) {
        if (type != null && status != null) {
            return repository.findByTypeAndStatus(type, status).stream()
                .map(PlanningInstrumentResponse::fromDomain).toList();
        }
        if (type != null) {
            return repository.findByType(type).stream()
                .map(PlanningInstrumentResponse::fromDomain).toList();
        }
        return repository.findAll().stream()
            .map(PlanningInstrumentResponse::fromDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanningInstrumentResponse findById(UUID id) {
        return repository.findById(id)
            .map(PlanningInstrumentResponse::fromDomain)
            .orElseThrow(() -> new ResourceNotFoundException("Instrumento de planificación no encontrado"));
    }

    @Override
    @Transactional
    public PlanningInstrumentResponse create(CreatePlanningInstrumentRequest request) {
        repository.findByTypeAndStartYearAndEndYear(request.type(), request.startYear(), request.endYear())
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Ya existe un instrumento " + request.type() + " para el periodo " + request.startYear() + "-" + request.endYear());
            });

        if (request.parentId() != null) {
            repository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Instrumento padre no encontrado"));
        }

        LocalDateTime now = LocalDateTime.now();
        PlanningInstrument instrument = new PlanningInstrument(
            UUID.randomUUID(),
            request.type(),
            request.name(),
            request.startYear(),
            request.endYear(),
            request.parentId(),
            request.approvedDate(),
            request.approvalRef(),
            InstrumentStatus.EN_FORMULACION,
            now,
            now
        );
        return PlanningInstrumentResponse.fromDomain(repository.save(instrument));
    }

    @Override
    @Transactional
    public PlanningInstrumentResponse update(UUID id, UpdatePlanningInstrumentRequest request) {
        PlanningInstrument existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Instrumento de planificación no encontrado"));

        PlanningInstrument updated = new PlanningInstrument(
            existing.id(),
            existing.type(),
            request.name(),
            request.startYear(),
            request.endYear(),
            existing.parentId(),
            request.approvedDate(),
            request.approvalRef(),
            request.status() != null ? request.status() : existing.status(),
            existing.createdAt(),
            LocalDateTime.now()
        );
        return PlanningInstrumentResponse.fromDomain(repository.save(updated));
    }
}
