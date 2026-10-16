package dev.demo.sisepp.tareas.domain.port.output;

import dev.demo.sisepp.tareas.domain.model.TaskSchedule;

import java.util.List;
import java.util.UUID;

public interface TaskScheduleRepositoryPort {
    TaskSchedule save(TaskSchedule schedule);
    List<TaskSchedule> findByTaskId(UUID taskId);
    List<TaskSchedule> findByActivityId(UUID activityId);
}
