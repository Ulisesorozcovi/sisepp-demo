package dev.demo.sisepp.shared.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record Notification(
    UUID id,
    UUID userId,
    String type,
    String title,
    String message,
    UUID referenceId,
    boolean read,
    LocalDateTime createdAt
) {}
