package dev.demo.sisepp.financiero.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinancialExecutionRequest(
    @NotNull(message = "Debe asociar la ejecución a un proyecto") UUID projectId,
    @NotNull @Min(1) @Max(12) Integer periodMonth,
    @NotNull @Min(2020) Integer periodYear,
    @NotNull BigDecimal approvedBudget,
    BigDecimal commitments,
    BigDecimal obligations,
    BigDecimal payments
) {}
