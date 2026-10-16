package dev.demo.sisepp.actividades.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateActivityRequest(
    @NotBlank(message = "El nombre de la Actividad es obligatorio") String name,
    @NotNull(message = "Debe asociar la Actividad a un Proyecto") UUID projectId,
    @NotEmpty(message = "Debe seleccionar al menos un indicador ODS") List<Integer> sdgIndicatorIds,
    String code,
    String measurementUnit,
    BigDecimal goal,
    BigDecimal weight,
    String indicatorDescription,
    String indicatorType,
    UUID thematicCategoryId,
    UUID managementIndicatorId,
    UUID measurementUnitId
) {}
