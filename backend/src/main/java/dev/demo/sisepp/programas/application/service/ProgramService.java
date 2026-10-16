package dev.demo.sisepp.programas.application.service;

import dev.demo.sisepp.programas.application.dto.CreateProgramRequest;
import dev.demo.sisepp.programas.application.dto.ProgramResponse;
import dev.demo.sisepp.programas.application.dto.UpdateProgramRequest;
import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.programas.application.port.input.ProgramUseCase;
import dev.demo.sisepp.programas.domain.port.output.ProgramRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.DuplicateNameException;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.shared.exception.WeightExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramService implements ProgramUseCase {

    private final ProgramRepositoryPort repository;

    private static final BigDecimal MAX_WEIGHT = new BigDecimal("100");

    @Override
    @Transactional
    public ProgramResponse create(CreateProgramRequest request) {
        if (repository.existsByStrategicLineIdAndNameIgnoreCase(request.strategicLineId(), request.name())) {
            throw new DuplicateNameException("Ya existe un Programa con ese nombre en esta Línea Estratégica");
        }
        validateWeight(request.strategicLineId(), request.weight(), null);

        Program program = new Program(
            null, request.name(), request.code(), request.weight(), request.description(),
            request.strategicLineId(), null, RecordStatus.ACTIVO, null, null
        );
        return ProgramResponse.fromDomain(repository.save(program));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> list(UUID strategicLineId) {
        List<Program> programs = strategicLineId != null
            ? repository.findByStrategicLineId(strategicLineId)
            : repository.findAll();
        return programs.stream().map(ProgramResponse::fromDomain).toList();
    }

    @Override
    @Transactional
    public ProgramResponse update(UUID id, UpdateProgramRequest request) {
        Program program = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado"));
        validateWeight(program.strategicLineId(), request.weight(), id);
        Program updated = new Program(
            program.id(), request.name(), request.code(), request.weight(), request.description(),
            program.strategicLineId(), program.pacId(), program.status(),
            program.createdAt(), program.updatedAt()
        );
        return ProgramResponse.fromDomain(repository.save(updated));
    }

    private void validateWeight(UUID strategicLineId, BigDecimal newWeight, UUID excludeId) {
        if (newWeight == null || strategicLineId == null) return;
        BigDecimal currentSum = repository.sumWeightsByStrategicLineId(strategicLineId, excludeId);
        BigDecimal total = currentSum.add(newWeight);
        if (total.compareTo(MAX_WEIGHT) > 0) {
            BigDecimal available = MAX_WEIGHT.subtract(currentSum).max(BigDecimal.ZERO);
            throw new WeightExceededException(
                "La ponderación total de los programas sería " + total +
                "%, excediendo el máximo de 100%. Peso disponible: " + available + "%"
            );
        }
    }
}
