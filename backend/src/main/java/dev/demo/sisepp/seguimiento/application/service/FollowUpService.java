package dev.demo.sisepp.seguimiento.application.service;

import dev.demo.sisepp.seguimiento.application.dto.CreateFollowUpRequest;
import dev.demo.sisepp.seguimiento.application.dto.FollowUpResponse;
import dev.demo.sisepp.seguimiento.application.dto.UpdateFollowUpRequest;
import dev.demo.sisepp.seguimiento.domain.model.FollowUp;
import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;
import dev.demo.sisepp.seguimiento.application.port.input.FollowUpUseCase;
import dev.demo.sisepp.seguimiento.domain.port.output.FollowUpRepositoryPort;
import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.shared.exception.DuplicateNameException;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.tareas.domain.model.Task;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;
import dev.demo.sisepp.tareas.domain.port.output.TaskRepositoryPort;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowUpService implements FollowUpUseCase {

    private final FollowUpRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final TaskRepositoryPort taskRepository;
    private final ActivityRepositoryPort activityRepository;
    private final ProjectRepositoryPort projectRepository;

    @Override
    @Transactional
    public FollowUpResponse create(CreateFollowUpRequest request, String requesterEmail) {
        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        validateOwnership(requester, request.taskId());

        if (repository.existsByTaskIdAndPeriod(request.taskId(), request.reportedYear(), request.reportedMonth())) {
            throw new DuplicateNameException(
                "Ya existe un seguimiento para esta tarea en el periodo " + request.reportedYear() + "-" + request.reportedMonth());
        }

        FollowUp followUp = new FollowUp(
            null, request.taskId(), request.reportedMonth(), request.reportedYear(),
            request.physicalProgress(), request.evidenceUrl(), request.attachmentUrl(),
            request.status() != null ? request.status() : FollowUpStatus.EN_PROCESO,
            request.observations(), requester.id(), null, null
        );
        FollowUp guardado = repository.save(followUp);
        sincronizarEstadoTarea(guardado.taskId());
        return FollowUpResponse.fromDomain(
            guardado, requester.fullName(), resolveTaskName(guardado.taskId()));
    }

    @Override
    @Transactional
    public FollowUpResponse update(UUID id, UpdateFollowUpRequest request, String requesterEmail) {
        FollowUp existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado"));

        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        validateOwnership(requester, existing.taskId());

        FollowUp updated = new FollowUp(
            existing.id(), existing.taskId(), existing.reportedMonth(), existing.reportedYear(),
            request.physicalProgress() != null ? request.physicalProgress() : existing.physicalProgress(),
            request.evidenceUrl() != null ? request.evidenceUrl() : existing.evidenceUrl(),
            request.attachmentUrl() != null ? request.attachmentUrl() : existing.attachmentUrl(),
            request.status() != null ? request.status() : existing.status(),
            request.observations() != null ? request.observations() : existing.observations(),
            existing.reportedByUserId(), existing.createdAt(), existing.updatedAt()
        );
        FollowUp saved = repository.save(updated);
        sincronizarEstadoTarea(saved.taskId());
        return FollowUpResponse.fromDomain(
            saved, resolveUserName(saved.reportedByUserId()), resolveTaskName(saved.taskId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse> listByTask(UUID taskId) {
        List<FollowUp> followUps = repository.findByTaskId(taskId);
        return toResponses(followUps);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse> listByPeriod(int year, int month) {
        List<FollowUp> followUps = repository.findByReportedYearAndReportedMonth(year, month);
        return toResponses(followUps);
    }

    @Override
    @Transactional
    public void delete(UUID id, String requesterEmail) {
        FollowUp existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado"));

        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        validateOwnership(requester, existing.taskId());

        UUID taskId = existing.taskId();
        repository.deleteById(id);
        // Al borrar un avance la tarea puede dejar de estar completada.
        sincronizarEstadoTarea(taskId);
    }

    private List<FollowUpResponse> toResponses(List<FollowUp> followUps) {
        if (followUps.isEmpty()) return List.of();
        List<UUID> userIds = followUps.stream()
            .map(FollowUp::reportedByUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<UUID, String> nameMap = userRepository.findByIdIn(userIds).stream()
            .collect(Collectors.toMap(User::id, User::fullName));

        // Resolución en bloque de los nombres de tarea: una sola consulta, no una por fila.
        List<UUID> taskIds = followUps.stream()
            .map(FollowUp::taskId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<UUID, String> taskMap = taskRepository.findByIdIn(taskIds).stream()
            .collect(Collectors.toMap(Task::id, Task::name));

        return followUps.stream()
            .map(f -> FollowUpResponse.fromDomain(
                f,
                nameMap.getOrDefault(f.reportedByUserId(), null),
                taskMap.getOrDefault(f.taskId(), null)))
            .toList();
    }

    /**
     * Ajusta el estado de la tarea al avance realmente reportado.
     *
     * <p>Es la transición automática del ciclo de vida: hasta ahora las tareas
     * nacían PROGRAMADA y nada volvía a moverlas, de modo que el estado no
     * reflejaba lo ejecutado.
     *
     * <ul>
     *   <li>Sin avance             -> PROGRAMADA</li>
     *   <li>Avance parcial         -> EN_EJECUCION</li>
     *   <li>Meta alcanzada         -> COMPLETADA, y se sella {@code completedAt}</li>
     * </ul>
     *
     * <p>No toca las tareas CANCELADA ni NO_APLICA: esas las cerró una persona
     * y el sistema no debe reabrirlas por su cuenta.
     *
     * <p>{@code completedAt} se fija una sola vez y solo se limpia si la tarea
     * deja de estar completada (por ejemplo al corregir a la baja un reporte),
     * para que la puntualidad no dependa de cuántas veces se editó el avance.
     */
    private void sincronizarEstadoTarea(UUID taskId) {
        if (taskId == null) return;

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return;
        if (task.status() == TaskStatus.CANCELADA || task.status() == TaskStatus.NO_APLICA) return;

        BigDecimal acumulado = repository.findByTaskId(taskId).stream()
            .map(FollowUp::physicalProgress)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal meta = task.physicalGoalValue();
        boolean metaAlcanzada = meta != null
            && meta.signum() > 0
            && acumulado.compareTo(meta) >= 0;

        TaskStatus nuevoEstado;
        LocalDateTime completadoEn;

        if (metaAlcanzada) {
            nuevoEstado = TaskStatus.COMPLETADA;
            completadoEn = task.completedAt() != null ? task.completedAt() : LocalDateTime.now();
        } else if (acumulado.signum() > 0) {
            nuevoEstado = TaskStatus.EN_EJECUCION;
            completadoEn = null;
        } else {
            nuevoEstado = TaskStatus.PROGRAMADA;
            completadoEn = null;
        }

        boolean sinCambios = task.status() == nuevoEstado
            && Objects.equals(task.completedAt(), completadoEn);
        if (sinCambios) return;

        taskRepository.save(new Task(
            task.id(), task.name(), task.activityId(),
            task.physicalGoalValue(), task.physicalGoalUnit(),
            task.dueDate(), task.responsibleName(), task.responsibleUserId(),
            task.taskType(), task.weight(), task.observation(),
            task.startDate(), nuevoEstado, completadoEn, task.riskNote(),
            task.createdAt(), task.updatedAt()
        ));
    }

    private String resolveTaskName(UUID taskId) {
        if (taskId == null) return null;
        return taskRepository.findById(taskId).map(Task::name).orElse(null);
    }

    private String resolveUserName(UUID userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).map(User::fullName).orElse(null);
    }

    private void validateOwnership(User requester, UUID taskId) {
        if (requester.role() == UserRole.STAFF) return;

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        if (requester.role() == UserRole.TECNICO) {
            if (!requester.id().equals(task.responsibleUserId())) {
                throw new AccessDeniedException("Solo puede reportar avance en tareas asignadas a usted");
            }
            return;
        }

        if (requester.role() == UserRole.COORDINADOR) {
            Activity activity = activityRepository.findById(task.activityId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
            boolean isCoordinator = projectRepository.findByCoordinatorId(requester.id()).stream()
                .anyMatch(p -> p.id().equals(activity.projectId()));
            if (!isCoordinator) {
                throw new AccessDeniedException("Solo puede reportar avance en proyectos asignados a usted");
            }
        }
    }
}
