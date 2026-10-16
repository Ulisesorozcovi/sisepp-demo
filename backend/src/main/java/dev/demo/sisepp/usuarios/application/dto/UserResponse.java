package dev.demo.sisepp.usuarios.application.dto;

import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String fullName,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
            user.id(),
            user.fullName(),
            user.email(),
            user.role(),
            user.status(),
            user.createdAt()
        );
    }
}
