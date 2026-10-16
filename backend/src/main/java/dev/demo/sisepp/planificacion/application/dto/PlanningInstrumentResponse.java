package dev.demo.sisepp.planificacion.application.dto;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;

import java.time.LocalDate;
import java.util.UUID;

public record PlanningInstrumentResponse(
    UUID id,
    InstrumentType type,
    String name,
    int startYear,
    int endYear,
    UUID parentId,
    LocalDate approvedDate,
    String approvalRef,
    InstrumentStatus status
) {
    public static PlanningInstrumentResponse fromDomain(PlanningInstrument p) {
        return new PlanningInstrumentResponse(
            p.id(), p.type(), p.name(), p.startYear(), p.endYear(),
            p.parentId(), p.approvedDate(), p.approvalRef(), p.status()
        );
    }
}
