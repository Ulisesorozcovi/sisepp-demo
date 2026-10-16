package dev.demo.sisepp.tareas.domain.port.output;

import dev.demo.sisepp.tareas.domain.model.TaskReschedule;

import java.util.List;
import java.util.UUID;

public interface TaskRescheduleRepositoryPort {
    TaskReschedule save(TaskReschedule reschedule);
    List<TaskReschedule> findByTaskIdOrderByChangedAtAsc(UUID taskId);
}
