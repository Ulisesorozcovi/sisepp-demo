package dev.demo.sisepp.actividades.application.dto;

import java.math.BigDecimal;

public record AnnualBudgetEntry(
    int year,
    BigDecimal budgetAmount
) {}
