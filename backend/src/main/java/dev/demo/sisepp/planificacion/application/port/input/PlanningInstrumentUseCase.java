package dev.demo.sisepp.planificacion.application.port.input;

import dev.demo.sisepp.planificacion.application.dto.CreatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.application.dto.PlanningInstrumentResponse;
import dev.demo.sisepp.planificacion.application.dto.UpdatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;

import java.util.List;
import java.util.UUID;

public interface PlanningInstrumentUseCase {
    List<PlanningInstrumentResponse> list(InstrumentType type, InstrumentStatus status);
    PlanningInstrumentResponse findById(UUID id);
    PlanningInstrumentResponse create(CreatePlanningInstrumentRequest request);
    PlanningInstrumentResponse update(UUID id, UpdatePlanningInstrumentRequest request);
}
