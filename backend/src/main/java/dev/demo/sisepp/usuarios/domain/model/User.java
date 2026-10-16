package dev.demo.sisepp.usuarios.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String fullName,
    String email,
    String passwordHash,
    UserRole role,
    UserStatus status,
    int failedLoginAttempts,
    LocalDateTime lockedUntil,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    public boolean isDirector() {
        return role == UserRole.DIRECTOR;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVO;
    }
}
