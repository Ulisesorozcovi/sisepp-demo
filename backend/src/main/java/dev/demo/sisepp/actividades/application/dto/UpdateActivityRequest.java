package dev.demo.sisepp.actividades.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateActivityRequest(
    @NotBlank(message = "El nombre de la Actividad es obligatorio") String name,
    @NotEmpty(message = "Debe seleccionar al menos un indicador ODS") List<Integer> sdgIndicatorIds,
    String code,
    String measurementUnit,
    BigDecimal goal,
    BigDecimal weight,
    String indicatorDescription,
    String indicatorType,
    UUID thematicCategoryId,
    UUID managementIndicatorId,
    UUID measurementUnitId,
    List<AnnualGoalEntry> annualGoals,
    List<AnnualBudgetEntry> annualBudgets
) {}
