package dev.demo.sisepp.shared.bootstrap;

import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaEntity;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.model.UserStatus;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        String temporaryPassword = UUID.randomUUID().toString().substring(0, 10);

        UserJpaEntity director = UserJpaEntity.builder()
            .fullName("Administrador SISEPP")
            .email("director@example.com")
            .passwordHash(passwordEncoder.encode(temporaryPassword))
            .role(UserRole.DIRECTOR)
            .status(UserStatus.ACTIVO)
            .build();

        userRepository.save(director);

        log.warn("========================================================");
        log.warn(" Usuario Director inicial creado");
        log.warn(" Correo: director@example.com");
        log.warn(" Contraseña temporal: {}", temporaryPassword);
        log.warn(" Cámbiala luego de tu primer inicio de sesión.");
        log.warn("========================================================");
    }
}
