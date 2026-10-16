package dev.demo.sisepp.catalogo.domain.port.output;

import dev.demo.sisepp.catalogo.domain.model.PublicInvestmentProgram;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicInvestmentProgramRepositoryPort {
    List<PublicInvestmentProgram> findActive();
    Optional<PublicInvestmentProgram> findById(UUID id);
    PublicInvestmentProgram save(PublicInvestmentProgram program);
}
