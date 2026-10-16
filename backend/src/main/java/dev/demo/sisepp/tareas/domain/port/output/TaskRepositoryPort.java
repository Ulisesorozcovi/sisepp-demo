package dev.demo.sisepp.tareas.domain.port.output;

import dev.demo.sisepp.tareas.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findByActivityId(UUID activityId);
    List<Task> findByActivityIdIn(List<UUID> activityIds);
    List<Task> findByCoordinatorId(UUID userId);
    List<Task> findByResponsibleUserId(UUID userId);
    List<Task> findByIdIn(List<UUID> ids);
    List<Task> findAll();
    void deleteById(UUID id);
}
