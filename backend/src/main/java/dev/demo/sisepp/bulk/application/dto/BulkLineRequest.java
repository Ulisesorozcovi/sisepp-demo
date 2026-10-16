package dev.demo.sisepp.bulk.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record BulkLineRequest(
    @NotBlank(message = "El nombre de la línea estratégica es obligatorio") String name,
    BigDecimal weight,
    @Valid List<BulkProgramRequest> programs
) {}
