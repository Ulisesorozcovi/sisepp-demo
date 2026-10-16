package dev.demo.sisepp.shared.notification;

import java.time.LocalDate;
import java.util.UUID;

public record TaskAssignedEvent(
    UUID responsibleUserId,
    String responsibleEmail,
    String responsibleFullName,
    UUID taskId,
    String taskName,
    LocalDate dueDate,
    String assignedByName
) {}
