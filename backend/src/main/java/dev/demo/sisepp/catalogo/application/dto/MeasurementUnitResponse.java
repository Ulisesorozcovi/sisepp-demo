package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;
import dev.demo.sisepp.shared.RecordStatus;

import java.util.UUID;

public record MeasurementUnitResponse(UUID id, int code, String name, RecordStatus status) {
    public static MeasurementUnitResponse fromDomain(MeasurementUnit m) {
        return new MeasurementUnitResponse(m.id(), m.code(), m.name(), m.status());
    }
}
