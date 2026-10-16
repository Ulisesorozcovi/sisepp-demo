package dev.demo.sisepp.bulk.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record BulkProgramRequest(
    @NotBlank(message = "El código del programa es obligatorio") String code,
    @NotBlank(message = "El nombre del programa es obligatorio") String name,
    BigDecimal weight,
    @Valid List<BulkProjectRequest> projects
) {}
