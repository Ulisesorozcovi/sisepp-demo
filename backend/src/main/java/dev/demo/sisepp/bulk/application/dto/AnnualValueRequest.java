package dev.demo.sisepp.bulk.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AnnualValueRequest(
    @NotNull(message = "El año es obligatorio") Integer year,
    @NotNull(message = "El valor es obligatorio") BigDecimal value
) {}
