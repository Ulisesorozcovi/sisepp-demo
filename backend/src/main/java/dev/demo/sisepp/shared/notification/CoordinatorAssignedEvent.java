package dev.demo.sisepp.shared.notification;

import java.util.UUID;

public record CoordinatorAssignedEvent(
    UUID userId,
    String userEmail,
    String userFullName,
    UUID projectId,
    String projectName
) {}
