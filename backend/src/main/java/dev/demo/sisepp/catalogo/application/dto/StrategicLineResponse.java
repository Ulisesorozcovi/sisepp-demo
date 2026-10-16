package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.StrategicLine;
import dev.demo.sisepp.shared.RecordStatus;

import java.util.UUID;

public record StrategicLineResponse(UUID id, String name, String description, RecordStatus status) {
    public static StrategicLineResponse fromDomain(StrategicLine line) {
        return new StrategicLineResponse(line.id(), line.name(), line.description(), line.status());
    }
}
