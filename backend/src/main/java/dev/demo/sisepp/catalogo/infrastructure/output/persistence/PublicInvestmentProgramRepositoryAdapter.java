package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.PublicInvestmentProgram;
import dev.demo.sisepp.catalogo.domain.port.output.PublicInvestmentProgramRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PublicInvestmentProgramRepositoryAdapter implements PublicInvestmentProgramRepositoryPort {

    private final PublicInvestmentProgramJpaRepository jpa;

    @Override
    public List<PublicInvestmentProgram> findActive() {
        return jpa.findByStatus(RecordStatus.ACTIVO).stream().map(PublicInvestmentProgramMapper::toDomain).toList();
    }

    @Override
    public Optional<PublicInvestmentProgram> findById(UUID id) {
        return jpa.findById(id).map(PublicInvestmentProgramMapper::toDomain);
    }

    @Override
    public PublicInvestmentProgram save(PublicInvestmentProgram program) {
        PublicInvestmentProgramJpaEntity entity;
        if (program.id() != null) {
            entity = jpa.findById(program.id()).orElseGet(() -> PublicInvestmentProgramMapper.toJpa(program));
            entity.setCode(program.code());
            entity.setName(program.name());
            entity.setSector(program.sector());
            entity.setSourceUrl(program.sourceUrl());
            entity.setStatus(program.status());
        } else {
            entity = PublicInvestmentProgramMapper.toJpa(program);
        }
        return PublicInvestmentProgramMapper.toDomain(jpa.save(entity));
    }
}
