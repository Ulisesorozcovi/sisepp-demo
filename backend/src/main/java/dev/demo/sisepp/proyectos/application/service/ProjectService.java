package dev.demo.sisepp.proyectos.application.service;

import dev.demo.sisepp.proyectos.application.dto.AssignCoordinatorRequest;
import dev.demo.sisepp.proyectos.application.dto.CreateProjectRequest;
import dev.demo.sisepp.proyectos.application.dto.ProjectResponse;
import dev.demo.sisepp.proyectos.application.dto.UpdateProjectRequest;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.application.port.input.ProjectUseCase;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.InvalidCoordinatorRoleException;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.shared.exception.WeightExceededException;
import dev.demo.sisepp.shared.notification.CoordinatorAssignedEvent;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectUseCase {

    private final ProjectRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        validateWeight(request.programId(), request.weight(), null);
        Project project = new Project(
            null, request.name(), request.programId(), request.vigencia(),
            request.code(), request.subdivision(), request.weight(),
            request.objective(), request.objectiveWeight(), request.pipId(),
            null, null, null, RecordStatus.ACTIVO, Set.of(), null, null
        );
        return ProjectResponse.fromDomain(repository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> list(UUID programId, Integer vigencia) {
        List<Project> projects;
        if (programId != null) {
            projects = repository.findByProgramId(programId);
        } else if (vigencia != null) {
            projects = repository.findByVigencia(vigencia);
        } else {
            projects = repository.findAll();
        }

        if (programId != null && vigencia != null) {
            projects = projects.stream().filter(p -> p.vigencia() == vigencia).toList();
        }

        return projects.stream().map(ProjectResponse::fromDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> listForCoordinator(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        return repository.findByCoordinatorId(user.id()).stream().map(ProjectResponse::fromDomain).toList();
    }

    @Override
    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request) {
        Project project = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        validateWeight(project.programId(), request.weight(), id);
        Project updated = new Project(
            project.id(), request.name(), project.programId(), request.vigencia(),
            request.code(), request.subdivision(), request.weight(),
            request.objective(), request.objectiveWeight(), request.pipId(),
            project.pacId(), project.startYear(), project.endYear(),
            project.status(), project.coordinatorIds(), project.createdAt(), project.updatedAt()
        );
        return ProjectResponse.fromDomain(repository.save(updated));
    }

    @Override
    @Transactional
    public ProjectResponse assignCoordinator(UUID projectId, AssignCoordinatorRequest request) {
        repository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.role() != UserRole.COORDINADOR) {
            throw new InvalidCoordinatorRoleException("Solo un usuario con rol Coordinador puede asignarse a un Proyecto");
        }

        repository.addCoordinator(projectId, user.id());

        Project project = repository.findById(projectId).orElseThrow();
        eventPublisher.publishEvent(new CoordinatorAssignedEvent(
                user.id(), user.email(), user.fullName(),
                project.id(), project.name()));

        return ProjectResponse.fromDomain(project);
    }

    @Override
    @Transactional
    public ProjectResponse removeCoordinator(UUID projectId, UUID userId) {
        repository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        repository.removeCoordinator(projectId, userId);
        return ProjectResponse.fromDomain(repository.findById(projectId).orElseThrow());
    }

    private static final BigDecimal MAX_WEIGHT = new BigDecimal("100");

    private void validateWeight(UUID programId, BigDecimal newWeight, UUID excludeId) {
        if (newWeight == null || programId == null) return;
        BigDecimal currentSum = repository.sumWeightsByProgramId(programId, excludeId);
        BigDecimal total = currentSum.add(newWeight);
        if (total.compareTo(MAX_WEIGHT) > 0) {
            BigDecimal available = MAX_WEIGHT.subtract(currentSum).max(BigDecimal.ZERO);
            throw new WeightExceededException(
                "La ponderación total de los proyectos sería " + total +
                "%, excediendo el máximo de 100%. Peso disponible: " + available + "%"
            );
        }
    }
}
