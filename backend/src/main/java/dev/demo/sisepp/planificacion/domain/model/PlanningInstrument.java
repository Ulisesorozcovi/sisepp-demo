package dev.demo.sisepp.planificacion.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PlanningInstrument(
    UUID id,
    InstrumentType type,
    String name,
    int startYear,
    int endYear,
    UUID parentId,
    LocalDate approvedDate,
    String approvalRef,
    InstrumentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public boolean coversYear(int year) {
        return year >= startYear && year <= endYear;
    }

    public int durationYears() {
        return endYear - startYear + 1;
    }
}
