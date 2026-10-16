package dev.demo.sisepp.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") String email,
    @NotBlank(message = "La contraseña es obligatoria") @Size(max = 72, message = "La contraseña no puede exceder 72 caracteres") String password
) {}
