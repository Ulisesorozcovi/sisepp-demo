package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.SdgIndicator;

public record SdgIndicatorResponse(Integer id, String code, String name, String description) {
    public static SdgIndicatorResponse fromDomain(SdgIndicator indicator) {
        return new SdgIndicatorResponse(indicator.id(), indicator.code(), indicator.name(), indicator.description());
    }
}
