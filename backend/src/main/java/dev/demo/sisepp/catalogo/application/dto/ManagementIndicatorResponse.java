package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.IndicatorType;
import dev.demo.sisepp.catalogo.domain.model.ManagementIndicator;
import dev.demo.sisepp.shared.RecordStatus;

import java.util.UUID;

public record ManagementIndicatorResponse(UUID id, String code, String name, String regulationRef, IndicatorType indicatorType, RecordStatus status) {
    public static ManagementIndicatorResponse fromDomain(ManagementIndicator m) {
        return new ManagementIndicatorResponse(m.id(), m.code(), m.name(), m.regulationRef(), m.indicatorType(), m.status());
    }
}
