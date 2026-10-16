package dev.demo.sisepp.financiero.infrastructure.output.persistence;

import dev.demo.sisepp.financiero.domain.model.FinancialExecution;

public final class FinancialExecutionMapper {

    private FinancialExecutionMapper() {}

    public static FinancialExecution toDomain(FinancialExecutionJpaEntity entity) {
        return new FinancialExecution(
            entity.getId(),
            entity.getProject() != null ? entity.getProject().getId() : null,
            entity.getPeriodMonth(),
            entity.getPeriodYear(),
            entity.getApprovedBudget(),
            entity.getCommitments(),
            entity.getObligations(),
            entity.getPayments(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static FinancialExecutionJpaEntity toJpa(FinancialExecution domain) {
        return FinancialExecutionJpaEntity.builder()
            .id(domain.id())
            .periodMonth(domain.periodMonth())
            .periodYear(domain.periodYear())
            .approvedBudget(domain.approvedBudget())
            .commitments(domain.commitments())
            .obligations(domain.obligations())
            .payments(domain.payments())
            .build();
    }
}
