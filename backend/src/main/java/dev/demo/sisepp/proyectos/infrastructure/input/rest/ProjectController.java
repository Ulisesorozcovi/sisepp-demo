package dev.demo.sisepp.proyectos.infrastructure.input.rest;

import dev.demo.sisepp.proyectos.application.dto.AssignCoordinatorRequest;
import dev.demo.sisepp.proyectos.application.dto.CreateProjectRequest;
import dev.demo.sisepp.proyectos.application.dto.ProjectResponse;
import dev.demo.sisepp.proyectos.application.dto.UpdateProjectRequest;
import dev.demo.sisepp.proyectos.application.port.input.ProjectUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structure/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<ProjectResponse>> list(
        @AuthenticationPrincipal String email,
        @RequestParam(required = false) UUID programId,
        @RequestParam(required = false) Integer vigencia,
        @RequestParam(required = false, defaultValue = "false") boolean mine) {
        if (mine) {
            return ResponseEntity.ok(service.listForCoordinator(email));
        }
        return ResponseEntity.ok(service.list(programId, vigencia));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/coordinators")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProjectResponse> assignCoordinator(
        @PathVariable UUID id, @Valid @RequestBody AssignCoordinatorRequest request) {
        return ResponseEntity.ok(service.assignCoordinator(id, request));
    }

    @DeleteMapping("/{id}/coordinators/{userId}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProjectResponse> removeCoordinator(@PathVariable UUID id, @PathVariable UUID userId) {
        return ResponseEntity.ok(service.removeCoordinator(id, userId));
    }
}
