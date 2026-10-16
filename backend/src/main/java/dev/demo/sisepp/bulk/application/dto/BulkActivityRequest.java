package dev.demo.sisepp.bulk.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record BulkActivityRequest(
    @NotBlank(message = "El código de la actividad es obligatorio") String code,
    @NotBlank(message = "El nombre de la actividad es obligatorio") String name,
    BigDecimal weight,
    String measurementUnit,
    String indicatorDescription,
    String indicatorType,
    Set<Integer> sdgIndicatorIds,
    @Valid List<AnnualValueRequest> annualGoals,
    @Valid List<AnnualValueRequest> annualBudgets
) {}
