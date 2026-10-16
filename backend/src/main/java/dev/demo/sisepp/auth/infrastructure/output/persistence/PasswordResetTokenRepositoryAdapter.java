package dev.demo.sisepp.auth.infrastructure.output.persistence;

import dev.demo.sisepp.auth.domain.model.PasswordResetToken;
import dev.demo.sisepp.auth.domain.port.output.PasswordResetTokenRepositoryPort;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = PasswordResetTokenMapper.toJpa(token);
        if (token.userId() != null) {
            userJpaRepository.findById(token.userId()).ifPresent(entity::setUser);
        }
        return PasswordResetTokenMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(PasswordResetTokenMapper::toDomain);
    }
}
