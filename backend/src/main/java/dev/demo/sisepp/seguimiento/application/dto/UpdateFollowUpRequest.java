package dev.demo.sisepp.seguimiento.application.dto;

import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;

import java.math.BigDecimal;

public record UpdateFollowUpRequest(
    BigDecimal physicalProgress,
    String evidenceUrl,
    String attachmentUrl,
    FollowUpStatus status,
    String observations
) {}
