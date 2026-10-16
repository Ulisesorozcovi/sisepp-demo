package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.infrastructure.output.persistence.ActivityJpaRepository;
import dev.demo.sisepp.tareas.domain.model.Task;
import dev.demo.sisepp.tareas.domain.port.output.TaskRepositoryPort;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository jpaRepository;
    private final ActivityJpaRepository activityJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity;
        if (task.id() != null) {
            entity = jpaRepository.findById(task.id()).orElseGet(() -> TaskMapper.toJpa(task));
            entity.setName(task.name());
            entity.setPhysicalGoalValue(task.physicalGoalValue());
            entity.setPhysicalGoalUnit(task.physicalGoalUnit());
            entity.setDueDate(task.dueDate());
            entity.setResponsibleName(task.responsibleName());
            entity.setTaskType(task.taskType());
            entity.setWeight(task.weight());
            entity.setObservation(task.observation());
            entity.setStartDate(task.startDate());
            entity.setStatus(task.status());
            entity.setCompletedAt(task.completedAt());
            entity.setRiskNote(task.riskNote());
        } else {
            entity = TaskMapper.toJpa(task);
        }
        if (task.activityId() != null) {
            activityJpaRepository.findById(task.activityId()).ifPresent(entity::setActivity);
        }
        if (task.responsibleUserId() != null) {
            userJpaRepository.findById(task.responsibleUserId()).ifPresent(entity::setResponsibleUser);
        }
        return TaskMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id).map(TaskMapper::toDomain);
    }

    @Override
    public List<Task> findByActivityId(UUID activityId) {
        return jpaRepository.findByActivityId(activityId).stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByActivityIdIn(List<UUID> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) return List.of();
        return jpaRepository.findByActivity_IdIn(activityIds).stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByCoordinatorId(UUID userId) {
        return jpaRepository.findByActivity_Project_CoordinatorEntries_UserId(userId).stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByResponsibleUserId(UUID userId) {
        return jpaRepository.findByResponsibleUser_Id(userId).stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return jpaRepository.findByIdIn(ids).stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream()
            .map(TaskMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
