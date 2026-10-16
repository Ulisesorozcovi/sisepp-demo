package dev.demo.sisepp.programas.application.port.input;

import dev.demo.sisepp.programas.application.dto.CreateProgramRequest;
import dev.demo.sisepp.programas.application.dto.ProgramResponse;
import dev.demo.sisepp.programas.application.dto.UpdateProgramRequest;

import java.util.List;
import java.util.UUID;

public interface ProgramUseCase {
    List<ProgramResponse> list(UUID strategicLineId);
    ProgramResponse create(CreateProgramRequest request);
    ProgramResponse update(UUID id, UpdateProgramRequest request);
}
