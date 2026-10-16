package dev.demo.sisepp.usuarios.domain.port.output;

import dev.demo.sisepp.usuarios.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<User> findByIdIn(List<UUID> ids);
    List<User> findAll();
    long count();
}
