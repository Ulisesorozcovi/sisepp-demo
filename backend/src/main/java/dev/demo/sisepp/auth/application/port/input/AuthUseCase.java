package dev.demo.sisepp.auth.application.port.input;

import dev.demo.sisepp.auth.application.dto.LoginRequest;
import dev.demo.sisepp.auth.application.dto.LoginResponse;
import dev.demo.sisepp.usuarios.domain.model.User;

public interface AuthUseCase {
    LoginResponse login(LoginRequest request);
    void forgotPassword(String email);
    void resetPassword(String rawToken, String newPassword);
    String issueActivationToken(User user);
}
