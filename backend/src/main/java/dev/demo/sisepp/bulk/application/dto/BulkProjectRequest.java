package dev.demo.sisepp.bulk.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record BulkProjectRequest(
    @NotBlank(message = "El código del proyecto es obligatorio") String code,
    @NotBlank(message = "El nombre del proyecto es obligatorio") String name,
    BigDecimal weight,
    @NotNull(message = "La vigencia es obligatoria") Integer vigencia,
    String subdivision,
    String objective,
    BigDecimal objectiveWeight,
    @Valid List<BulkActivityRequest> activities
) {}
