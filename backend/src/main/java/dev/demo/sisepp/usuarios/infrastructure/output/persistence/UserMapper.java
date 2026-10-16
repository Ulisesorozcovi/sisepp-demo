package dev.demo.sisepp.usuarios.infrastructure.output.persistence;

import dev.demo.sisepp.usuarios.domain.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserJpaEntity entity) {
        return new User(
            entity.getId(),
            entity.getFullName(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getRole(),
            entity.getStatus(),
            entity.getFailedLoginAttempts() != null ? entity.getFailedLoginAttempts() : 0,
            entity.getLockedUntil(),
            entity.getLastLoginAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static UserJpaEntity toJpa(User domain) {
        return UserJpaEntity.builder()
            .id(domain.id())
            .fullName(domain.fullName())
            .email(domain.email())
            .passwordHash(domain.passwordHash())
            .role(domain.role())
            .status(domain.status())
            .failedLoginAttempts(domain.failedLoginAttempts())
            .lockedUntil(domain.lockedUntil())
            .lastLoginAt(domain.lastLoginAt())
            .build();
    }
}
