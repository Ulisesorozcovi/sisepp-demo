package dev.demo.sisepp.usuarios.application.dto;

import dev.demo.sisepp.usuarios.domain.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
    @NotBlank(message = "El nombre completo es obligatorio") String fullName,
    @NotNull(message = "Debe seleccionar un rol") UserRole role
) {}
