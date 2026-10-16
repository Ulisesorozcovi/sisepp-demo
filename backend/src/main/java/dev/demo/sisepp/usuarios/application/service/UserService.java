package dev.demo.sisepp.usuarios.application.service;

import dev.demo.sisepp.auth.application.port.input.AuthUseCase;
import dev.demo.sisepp.auth.application.service.EmailService;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.model.UserStatus;
import dev.demo.sisepp.usuarios.application.dto.CreateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UpdateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UserResponse;
import dev.demo.sisepp.shared.exception.CannotInactivateDirectorException;
import dev.demo.sisepp.shared.exception.DuplicateEmailException;
import dev.demo.sisepp.shared.exception.UserNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import dev.demo.sisepp.usuarios.application.port.input.UserUseCase;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUseCase authService;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmailException("Este correo ya se encuentra registrado");
        }

        String temporaryPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());

        User user = new User(
            null, request.fullName(), request.email(), temporaryPasswordHash,
            request.role(), UserStatus.ACTIVO, 0, null, null, null, null
        );

        user = userRepository.save(user);

        String activationToken = authService.issueActivationToken(user);
        emailService.sendActivationEmail(user, activationToken);

        return UserResponse.fromDomain(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(String role, String status, String search) {
        String roleFilter = role == null ? null : role.trim();
        String statusFilter = status == null ? null : status.trim();
        String searchFilter = search == null ? null : search.trim().toLowerCase();

        return userRepository.findAll().stream()
            .filter(u -> roleFilter == null || roleFilter.isBlank() || u.role().name().equalsIgnoreCase(roleFilter))
            .filter(u -> statusFilter == null || statusFilter.isBlank() || u.status().name().equalsIgnoreCase(statusFilter))
            .filter(u -> searchFilter == null || searchFilter.isBlank()
                || u.fullName().toLowerCase().contains(searchFilter)
                || u.email().toLowerCase().contains(searchFilter))
            .map(UserResponse::fromDomain)
            .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String callerEmail) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.email().equalsIgnoreCase(callerEmail) && request.role() != user.role()) {
            throw new AccessDeniedException("No puede cambiar su propio rol");
        }

        User updated = new User(
            user.id(), request.fullName(), user.email(), user.passwordHash(),
            request.role(), user.status(), user.failedLoginAttempts(), user.lockedUntil(),
            user.lastLoginAt(), user.createdAt(), user.updatedAt()
        );
        updated = userRepository.save(updated);
        return UserResponse.fromDomain(updated);
    }

    @Override
    @Transactional
    public UserResponse inactivateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.role() == UserRole.DIRECTOR) {
            throw new CannotInactivateDirectorException("No se puede inactivar a un usuario con rol Director.");
        }

        User updated = new User(
            user.id(), user.fullName(), user.email(), user.passwordHash(),
            user.role(), UserStatus.INACTIVO, user.failedLoginAttempts(), user.lockedUntil(),
            user.lastLoginAt(), user.createdAt(), user.updatedAt()
        );
        updated = userRepository.save(updated);
        return UserResponse.fromDomain(updated);
    }

    @Override
    @Transactional
    public UserResponse reactivateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        User updated = new User(
            user.id(), user.fullName(), user.email(), user.passwordHash(),
            user.role(), UserStatus.ACTIVO, 0, null,
            user.lastLoginAt(), user.createdAt(), user.updatedAt()
        );
        updated = userRepository.save(updated);
        return UserResponse.fromDomain(updated);
    }
}
