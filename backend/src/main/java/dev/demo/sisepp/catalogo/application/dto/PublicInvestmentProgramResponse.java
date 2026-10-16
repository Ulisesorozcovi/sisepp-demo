package dev.demo.sisepp.catalogo.application.dto;

import dev.demo.sisepp.catalogo.domain.model.PublicInvestmentProgram;
import dev.demo.sisepp.shared.RecordStatus;

import java.util.UUID;

public record PublicInvestmentProgramResponse(UUID id, String code, String name, String sector, String sourceUrl, RecordStatus status) {
    public static PublicInvestmentProgramResponse fromDomain(PublicInvestmentProgram p) {
        return new PublicInvestmentProgramResponse(p.id(), p.code(), p.name(), p.sector(), p.sourceUrl(), p.status());
    }
}
