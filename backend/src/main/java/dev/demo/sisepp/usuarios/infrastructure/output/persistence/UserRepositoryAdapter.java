package dev.demo.sisepp.usuarios.infrastructure.output.persistence;

import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserMapper.toJpa(user);
        return UserMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return jpaRepository.findByEmailIgnoreCase(email).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public List<User> findByIdIn(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        return jpaRepository.findByIdIn(ids).stream()
            .map(UserMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
            .map(UserMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
