package dev.demo.sisepp.financiero.infrastructure.output.persistence;

import dev.demo.sisepp.financiero.domain.model.FinancialExecution;
import dev.demo.sisepp.financiero.domain.port.output.FinancialExecutionRepositoryPort;
import dev.demo.sisepp.proyectos.infrastructure.output.persistence.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FinancialExecutionRepositoryAdapter implements FinancialExecutionRepositoryPort {

    private final FinancialExecutionJpaRepository jpaRepository;
    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public FinancialExecution save(FinancialExecution execution) {
        FinancialExecutionJpaEntity entity;
        if (execution.id() != null) {
            entity = jpaRepository.findById(execution.id())
                .orElseGet(() -> FinancialExecutionMapper.toJpa(execution));
            entity.setPeriodMonth(execution.periodMonth());
            entity.setPeriodYear(execution.periodYear());
            entity.setApprovedBudget(execution.approvedBudget());
            entity.setCommitments(execution.commitments());
            entity.setObligations(execution.obligations());
            entity.setPayments(execution.payments());
        } else {
            entity = FinancialExecutionMapper.toJpa(execution);
        }
        if (execution.projectId() != null) {
            projectJpaRepository.findById(execution.projectId()).ifPresent(entity::setProject);
        }
        return FinancialExecutionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<FinancialExecution> findById(UUID id) {
        return jpaRepository.findById(id).map(FinancialExecutionMapper::toDomain);
    }

    @Override
    public List<FinancialExecution> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
            .map(FinancialExecutionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FinancialExecution> findByPeriodYearAndPeriodMonth(Integer year, Integer month) {
        return jpaRepository.findByPeriodYearAndPeriodMonth(year, month).stream()
            .map(FinancialExecutionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
