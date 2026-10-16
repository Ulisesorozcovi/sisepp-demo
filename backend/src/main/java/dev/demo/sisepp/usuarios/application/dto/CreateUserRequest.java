package dev.demo.sisepp.usuarios.application.dto;

import dev.demo.sisepp.usuarios.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
    @NotBlank(message = "El nombre completo es obligatorio") String fullName,
    @NotBlank(message = "El correo es obligatorio") @Email(message = "Debe ser un correo institucional válido") String email,
    @NotNull(message = "Debe seleccionar un rol") UserRole role
) {}
