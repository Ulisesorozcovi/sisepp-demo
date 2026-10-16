package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.ThematicCategory;
import dev.demo.sisepp.shared.RecordStatus;

import java.util.UUID;

public record ThematicCategoryResponse(UUID id, String name, RecordStatus status) {
    public static ThematicCategoryResponse fromDomain(ThematicCategory t) {
        return new ThematicCategoryResponse(t.id(), t.name(), t.status());
    }
}
