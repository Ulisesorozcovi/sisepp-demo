package dev.demo.sisepp.financiero.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialExecution(
    UUID id,
    UUID projectId,
    int periodMonth,
    int periodYear,
    BigDecimal approvedBudget,
    BigDecimal commitments,
    BigDecimal obligations,
    BigDecimal payments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public BigDecimal availableBalance() {
        return approvedBudget.subtract(commitments);
    }

    public BigDecimal executionPercentage() {
        if (approvedBudget.signum() == 0) return BigDecimal.ZERO;
        return payments.multiply(BigDecimal.valueOf(100))
            .divide(approvedBudget, 2, RoundingMode.HALF_UP);
    }
}
