package dev.demo.sisepp.auth.application.service;

import dev.demo.sisepp.usuarios.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "sisepp.mail.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingEmailService implements EmailService {

    private final String resetUrl;

    public LoggingEmailService(@Value("${sisepp.frontend.base-url}") String frontendBaseUrl) {
        this.resetUrl = frontendBaseUrl + "/reset-password?token=";
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        log.info("[EMAIL] Bienvenida/activación para {} -> {}{}", user.email(), resetUrl, token);
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        log.info("[EMAIL] Recuperación de contraseña para {} -> {}{}", user.email(), resetUrl, token);
    }
}
