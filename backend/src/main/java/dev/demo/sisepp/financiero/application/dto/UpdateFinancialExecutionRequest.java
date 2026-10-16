package dev.demo.sisepp.financiero.application.dto;

import java.math.BigDecimal;

public record UpdateFinancialExecutionRequest(
    BigDecimal approvedBudget,
    BigDecimal commitments,
    BigDecimal obligations,
    BigDecimal payments
) {}
