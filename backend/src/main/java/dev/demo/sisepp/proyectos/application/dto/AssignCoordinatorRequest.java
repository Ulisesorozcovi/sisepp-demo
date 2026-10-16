package dev.demo.sisepp.proyectos.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCoordinatorRequest(
    @NotNull(message = "Debe seleccionar un Coordinador") UUID userId
) {}
