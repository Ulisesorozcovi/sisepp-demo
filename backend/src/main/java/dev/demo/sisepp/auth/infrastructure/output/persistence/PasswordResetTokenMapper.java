package dev.demo.sisepp.auth.infrastructure.output.persistence;

import dev.demo.sisepp.auth.domain.model.PasswordResetToken;

public final class PasswordResetTokenMapper {

    private PasswordResetTokenMapper() {}

    public static PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        return new PasswordResetToken(
            entity.getId(),
            entity.getUser() != null ? entity.getUser().getId() : null,
            entity.getToken(),
            entity.getExpiresAt(),
            entity.isUsed(),
            entity.getCreatedAt()
        );
    }

    public static PasswordResetTokenJpaEntity toJpa(PasswordResetToken domain) {
        return PasswordResetTokenJpaEntity.builder()
            .id(domain.id())
            .token(domain.token())
            .expiresAt(domain.expiresAt())
            .used(domain.used())
            .build();
    }
}
