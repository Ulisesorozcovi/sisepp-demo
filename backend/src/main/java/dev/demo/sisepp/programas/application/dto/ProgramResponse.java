package dev.demo.sisepp.programas.application.dto;

import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProgramResponse(
    UUID id,
    String name,
    String code,
    BigDecimal weight,
    String description,
    UUID strategicLineId,
    RecordStatus status
) {
    public static ProgramResponse fromDomain(Program program) {
        return new ProgramResponse(
            program.id(),
            program.name(),
            program.code(),
            program.weight(),
            program.description(),
            program.strategicLineId(),
            program.status()
        );
    }
}
