package dev.demo.sisepp.financiero.application.dto;

import dev.demo.sisepp.financiero.domain.model.FinancialExecution;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialExecutionResponse(
    UUID id,
    UUID projectId,
    int periodMonth,
    int periodYear,
    BigDecimal approvedBudget,
    BigDecimal commitments,
    BigDecimal obligations,
    BigDecimal payments,
    BigDecimal availableBalance,
    BigDecimal executionPercentage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static FinancialExecutionResponse fromDomain(FinancialExecution e) {
        return new FinancialExecutionResponse(
            e.id(), e.projectId(), e.periodMonth(), e.periodYear(),
            e.approvedBudget(), e.commitments(), e.obligations(), e.payments(),
            e.availableBalance(), e.executionPercentage(),
            e.createdAt(), e.updatedAt()
        );
    }
}
