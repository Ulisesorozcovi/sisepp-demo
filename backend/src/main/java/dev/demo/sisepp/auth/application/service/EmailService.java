package dev.demo.sisepp.auth.application.service;

import dev.demo.sisepp.usuarios.domain.model.User;

public interface EmailService {
    void sendActivationEmail(User user, String token);
    void sendPasswordResetEmail(User user, String token);
}
