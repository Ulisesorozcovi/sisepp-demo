package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.tareas.domain.model.TaskReschedule;
import dev.demo.sisepp.tareas.domain.port.output.TaskRescheduleRepositoryPort;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskRescheduleRepositoryAdapter implements TaskRescheduleRepositoryPort {

    private final TaskRescheduleJpaRepository jpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public TaskReschedule save(TaskReschedule reschedule) {
        TaskRescheduleJpaEntity entity = TaskRescheduleMapper.toJpa(reschedule);
        if (reschedule.taskId() != null) {
            taskJpaRepository.findById(reschedule.taskId()).ifPresent(entity::setTask);
        }
        if (reschedule.changedByUserId() != null) {
            userJpaRepository.findById(reschedule.changedByUserId()).ifPresent(entity::setChangedByUser);
        }
        return TaskRescheduleMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<TaskReschedule> findByTaskIdOrderByChangedAtAsc(UUID taskId) {
        return jpaRepository.findByTaskIdOrderByChangedAtAsc(taskId).stream()
            .map(TaskRescheduleMapper::toDomain)
            .collect(Collectors.toList());
    }
}
