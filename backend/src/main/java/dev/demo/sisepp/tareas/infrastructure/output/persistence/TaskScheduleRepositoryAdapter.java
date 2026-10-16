package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.infrastructure.output.persistence.ActivityJpaRepository;
import dev.demo.sisepp.tareas.domain.model.TaskSchedule;
import dev.demo.sisepp.tareas.domain.port.output.TaskScheduleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskScheduleRepositoryAdapter implements TaskScheduleRepositoryPort {

    private final TaskScheduleJpaRepository jpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public TaskSchedule save(TaskSchedule schedule) {
        TaskScheduleJpaEntity entity = TaskScheduleMapper.toJpa(schedule);
        if (schedule.taskId() != null) {
            taskJpaRepository.findById(schedule.taskId()).ifPresent(entity::setTask);
        }
        if (schedule.activityId() != null) {
            activityJpaRepository.findById(schedule.activityId()).ifPresent(entity::setActivity);
        }
        return TaskScheduleMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<TaskSchedule> findByTaskId(UUID taskId) {
        return jpaRepository.findByTaskId(taskId).stream()
            .map(TaskScheduleMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskSchedule> findByActivityId(UUID activityId) {
        return jpaRepository.findByActivityId(activityId).stream()
            .map(TaskScheduleMapper::toDomain)
            .collect(Collectors.toList());
    }
}
