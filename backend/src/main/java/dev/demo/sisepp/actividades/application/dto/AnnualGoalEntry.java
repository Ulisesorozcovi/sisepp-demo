package dev.demo.sisepp.actividades.application.dto;

import java.math.BigDecimal;

public record AnnualGoalEntry(
    int year,
    BigDecimal goalValue
) {}
