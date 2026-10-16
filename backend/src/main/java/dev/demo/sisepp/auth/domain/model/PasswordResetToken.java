package dev.demo.sisepp.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PasswordResetToken(
    UUID id,
    UUID userId,
    String token,
    LocalDateTime expiresAt,
    boolean used,
    LocalDateTime createdAt
) {
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}
