package dev.demo.sisepp.tareas.application.service;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.seguimiento.domain.model.FollowUp;
import dev.demo.sisepp.seguimiento.domain.port.output.FollowUpRepositoryPort;
import dev.demo.sisepp.tareas.application.dto.CreateTaskRequest;
import dev.demo.sisepp.tareas.application.dto.RescheduleTaskRequest;
import dev.demo.sisepp.tareas.application.dto.TaskResponse;
import dev.demo.sisepp.tareas.application.dto.TaskWithProgressResponse;
import dev.demo.sisepp.tareas.domain.model.Task;
import dev.demo.sisepp.tareas.domain.model.TaskReschedule;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;
import dev.demo.sisepp.tareas.application.port.input.TaskUseCase;
import dev.demo.sisepp.tareas.domain.port.output.TaskRepositoryPort;
import dev.demo.sisepp.tareas.domain.port.output.TaskRescheduleRepositoryPort;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.model.UserStatus;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.shared.notification.TaskAssignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ActivityRepositoryPort activityRepository;
    private final TaskRescheduleRepositoryPort rescheduleRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectRepositoryPort projectRepository;
    private final FollowUpRepositoryPort followUpRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TaskResponse create(CreateTaskRequest request, String requesterEmail) {
        Activity activity = activityRepository.findById(request.activityId())
            .orElseThrow(() -> new ResourceNotFoundException("Debe asociar la tarea a una Actividad existente"));

        User requester = currentUser(requesterEmail);
        assertCoordinatorOwnsProject(requester, activity.projectId());
        validateResponsibleUser(request.responsibleUserId());

        Task task = new Task(
            null, request.name(), request.activityId(),
            request.physicalGoalValue(), request.physicalGoalUnit(),
            request.dueDate(), request.responsibleName(), request.responsibleUserId(),
            request.taskType(), request.weight(), request.observation(),
            request.startDate(), TaskStatus.PROGRAMADA, null, null, null, null
        );
        Task saved = taskRepository.save(task);

        if (request.responsibleUserId() != null) {
            User responsible = userRepository.findById(request.responsibleUserId()).orElse(null);
            if (responsible != null) {
                eventPublisher.publishEvent(new TaskAssignedEvent(
                        responsible.id(), responsible.email(), responsible.fullName(),
                        saved.id(), saved.name(), saved.dueDate(),
                        requester.fullName()));
            }
        }

        return TaskResponse.fromDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> list(
        String requesterEmail, UUID projectId, UUID activityId, TaskStatus status,
        String responsible, LocalDate from, LocalDate to
    ) {
        User requester = currentUser(requesterEmail);

        List<Task> tasks = switch (requester.role()) {
            case COORDINADOR -> taskRepository.findByCoordinatorId(requester.id());
            case TECNICO -> taskRepository.findByResponsibleUserId(requester.id());
            default -> taskRepository.findAll();
        };

        Set<UUID> projectActivityIds = null;
        if (projectId != null) {
            projectActivityIds = activityRepository.findByProjectId(projectId).stream()
                .map(Activity::id)
                .collect(Collectors.toSet());
        }
        final Set<UUID> validActivityIds = projectActivityIds;

        return tasks.stream()
            .filter(t -> activityId == null || t.activityId().equals(activityId))
            .filter(t -> validActivityIds == null || validActivityIds.contains(t.activityId()))
            .filter(t -> status == null || t.status() == status)
            .filter(t -> responsible == null || responsible.isBlank()
                || t.responsibleName().toLowerCase().contains(responsible.toLowerCase()))
            .filter(t -> from == null || !t.dueDate().isBefore(from))
            .filter(t -> to == null || !t.dueDate().isAfter(to))
            .sorted((a, b) -> a.dueDate().compareTo(b.dueDate()))
            .map(TaskResponse::fromDomain)
            .toList();
    }

    @Override
    @Transactional
    public TaskResponse reschedule(UUID taskId, RescheduleTaskRequest request, String requesterEmail) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        User requester = currentUser(requesterEmail);
        Activity activity = activityRepository.findById(task.activityId()).orElse(null);
        if (activity != null) {
            assertCoordinatorOwnsProject(requester, activity.projectId());
        }

        TaskReschedule reschedule = new TaskReschedule(
            null, task.id(), task.dueDate(), request.newDueDate(),
            request.justification(), requester.id(), null
        );
        rescheduleRepository.save(reschedule);

        Task updated = new Task(
            task.id(), task.name(), task.activityId(),
            task.physicalGoalValue(), task.physicalGoalUnit(),
            request.newDueDate(), task.responsibleName(), task.responsibleUserId(),
            task.taskType(), task.weight(), task.observation(),
            task.startDate(), task.status(), task.completedAt(),
            // El plazo nuevo deja sin vigencia la advertencia anterior; el motivo
            // queda registrado en task_reschedules, que sí es el historial.
            null,
            task.createdAt(), task.updatedAt()
        );
        return TaskResponse.fromDomain(taskRepository.save(updated));
    }

    private User currentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private void assertCoordinatorOwnsProject(User requester, UUID projectId) {
        if (requester.role() != UserRole.COORDINADOR) return;
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null || !project.hasCoordinator(requester.id())) {
            throw new AccessDeniedException("No tiene permisos para editar tareas de este proyecto.");
        }
    }

    private void validateResponsibleUser(UUID userId) {
        if (userId == null) return;
        User responsible = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("El usuario responsable no existe"));
        if (responsible.status() != UserStatus.ACTIVO) {
            throw new IllegalArgumentException("El usuario responsable no está activo");
        }
        if (responsible.role() != UserRole.COORDINADOR && responsible.role() != UserRole.TECNICO) {
            throw new IllegalArgumentException("El usuario responsable debe tener rol Coordinador o Técnico");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskWithProgressResponse> listWithProgress(UUID activityId) {
        return listWithProgress(activityId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskWithProgressResponse> listWithProgress(UUID activityId, UUID projectId) {
        List<Task> tasks;
        Map<UUID, String> activityNames;
        if (activityId != null) {
            tasks = taskRepository.findByActivityId(activityId);
            String aName = activityRepository.findById(activityId).map(Activity::name).orElse("");
            activityNames = Map.of(activityId, aName);
        } else if (projectId != null) {
            List<Activity> activities = activityRepository.findByProjectId(projectId);
            activityNames = activities.stream().collect(Collectors.toMap(Activity::id, Activity::name));
            List<UUID> actIds = activities.stream().map(Activity::id).toList();
            tasks = taskRepository.findByActivityIdIn(actIds);
        } else {
            return List.of();
        }
        if (tasks.isEmpty()) return List.of();

        List<UUID> taskIds = tasks.stream().map(Task::id).toList();
        List<FollowUp> allFollowUps = followUpRepository.findByTaskIdIn(taskIds);

        Map<UUID, List<FollowUp>> followUpsByTask = allFollowUps.stream()
            .collect(Collectors.groupingBy(FollowUp::taskId));

        return tasks.stream().map(task -> {
            List<FollowUp> fus = followUpsByTask.getOrDefault(task.id(), List.of());

            BigDecimal executed = fus.stream()
                .map(fu -> fu.physicalProgress() != null ? fu.physicalProgress() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pct = BigDecimal.ZERO;
            if (task.physicalGoalValue() != null && task.physicalGoalValue().compareTo(BigDecimal.ZERO) > 0) {
                pct = executed.multiply(new BigDecimal("100"))
                    .divide(task.physicalGoalValue(), 2, RoundingMode.HALF_UP);
                if (pct.compareTo(new BigDecimal("100")) > 0) {
                    pct = new BigDecimal("100.00");
                }
            }

            FollowUp latest = fus.stream()
                .max(Comparator.comparing(FollowUp::createdAt))
                .orElse(null);

            return new TaskWithProgressResponse(
                task.id(),
                task.name(),
                task.responsibleName(),
                task.physicalGoalValue(),
                task.physicalGoalUnit(),
                executed,
                pct,
                latest != null ? latest.status() : null,
                latest != null ? latest.createdAt() : null,
                task.startDate(),
                task.dueDate(),
                task.status(),
                task.activityId(),
                activityNames.getOrDefault(task.activityId(), "")
            );
        }).toList();
    }

    @Override
    @Transactional
    public void delete(UUID taskId) {
        taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
        taskRepository.deleteById(taskId);
    }

}
