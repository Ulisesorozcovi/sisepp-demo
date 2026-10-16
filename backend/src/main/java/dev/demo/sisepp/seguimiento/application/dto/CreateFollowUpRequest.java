package dev.demo.sisepp.seguimiento.application.dto;

import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFollowUpRequest(
    @NotNull(message = "Debe asociar el seguimiento a una tarea") UUID taskId,
    @NotNull @Min(1) @Max(12) Integer reportedMonth,
    @NotNull @Min(2020) Integer reportedYear,
    BigDecimal physicalProgress,
    String evidenceUrl,
    String attachmentUrl,
    FollowUpStatus status,
    String observations
) {}
