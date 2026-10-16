package dev.demo.sisepp.auth.application.service;

import dev.demo.sisepp.auth.domain.model.PasswordResetToken;
import dev.demo.sisepp.auth.application.port.input.AuthUseCase;
import dev.demo.sisepp.auth.domain.port.output.PasswordResetTokenRepositoryPort;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserStatus;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import dev.demo.sisepp.auth.application.dto.LoginRequest;
import dev.demo.sisepp.auth.application.dto.LoginResponse;
import dev.demo.sisepp.shared.exception.AccountInactiveException;
import dev.demo.sisepp.shared.exception.AccountLockedException;
import dev.demo.sisepp.shared.exception.BadCredentialsAppException;
import dev.demo.sisepp.shared.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordResetTokenRepositoryPort resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${sisepp.login.max-failed-attempts}")
    private int maxFailedAttempts;

    @Value("${sisepp.login.lock-minutes}")
    private int lockMinutes;

    @Value("${sisepp.password-reset.token-expiration-minutes}")
    private int resetTokenExpirationMinutes;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new BadCredentialsAppException("Usuario o contraseña incorrectos"));

        if (user.isLocked()) {
            throw new AccountLockedException(
                "Cuenta bloqueada temporalmente por múltiples intentos fallidos. Intenta nuevamente en unos minutos.");
        }

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            registerFailedAttempt(user);
            throw new BadCredentialsAppException("Usuario o contraseña incorrectos");
        }

        if (user.status() == UserStatus.INACTIVO) {
            throw new AccountInactiveException("Su cuenta se encuentra inactiva. Contacte al Director del sistema.");
        }

        User updated = new User(
            user.id(), user.fullName(), user.email(), user.passwordHash(),
            user.role(), user.status(), 0, null,
            LocalDateTime.now(), user.createdAt(), user.updatedAt()
        );
        userRepository.save(updated);

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.fullName(), user.role().name(), user.email());
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.failedLoginAttempts() + 1;
        LocalDateTime lockedUntil = user.lockedUntil();
        if (attempts >= maxFailedAttempts) {
            lockedUntil = LocalDateTime.now().plusMinutes(lockMinutes);
        }
        User updated = new User(
            user.id(), user.fullName(), user.email(), user.passwordHash(),
            user.role(), user.status(), attempts, lockedUntil,
            user.lastLoginAt(), user.createdAt(), user.updatedAt()
        );
        userRepository.save(updated);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String rawToken = issueActivationToken(user);
            emailService.sendPasswordResetEmail(user, rawToken);
        });
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(rawToken)
            .orElseThrow(() -> new InvalidTokenException("El enlace de restablecimiento no es válido."));

        if (resetToken.used() || resetToken.isExpired()) {
            throw new InvalidTokenException("El enlace de restablecimiento venció o ya fue utilizado.");
        }

        User user = userRepository.findById(resetToken.userId())
            .orElseThrow(() -> new InvalidTokenException("Usuario asociado al token no encontrado."));

        User updated = new User(
            user.id(), user.fullName(), user.email(), passwordEncoder.encode(newPassword),
            user.role(), user.status(), 0, null,
            user.lastLoginAt(), user.createdAt(), user.updatedAt()
        );
        userRepository.save(updated);

        PasswordResetToken usedToken = new PasswordResetToken(
            resetToken.id(), resetToken.userId(), resetToken.token(),
            resetToken.expiresAt(), true, resetToken.createdAt()
        );
        resetTokenRepository.save(usedToken);
    }

    @Override
    @Transactional
    public String issueActivationToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
            null, user.id(), rawToken,
            LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes), false, null
        );
        resetTokenRepository.save(resetToken);
        return rawToken;
    }
}
