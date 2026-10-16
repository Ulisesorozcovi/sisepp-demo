package dev.demo.sisepp.proyectos.application.port.input;

import dev.demo.sisepp.proyectos.application.dto.AssignCoordinatorRequest;
import dev.demo.sisepp.proyectos.application.dto.CreateProjectRequest;
import dev.demo.sisepp.proyectos.application.dto.ProjectResponse;
import dev.demo.sisepp.proyectos.application.dto.UpdateProjectRequest;

import java.util.List;
import java.util.UUID;

public interface ProjectUseCase {
    List<ProjectResponse> list(UUID programId, Integer vigencia);
    List<ProjectResponse> listForCoordinator(String email);
    ProjectResponse create(CreateProjectRequest request);
    ProjectResponse update(UUID id, UpdateProjectRequest request);
    ProjectResponse assignCoordinator(UUID projectId, AssignCoordinatorRequest request);
    ProjectResponse removeCoordinator(UUID projectId, UUID userId);
}
