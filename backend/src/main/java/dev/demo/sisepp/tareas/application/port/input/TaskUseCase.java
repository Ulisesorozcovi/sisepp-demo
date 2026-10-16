package dev.demo.sisepp.tareas.application.port.input;

import dev.demo.sisepp.tareas.application.dto.CreateTaskRequest;
import dev.demo.sisepp.tareas.application.dto.RescheduleTaskRequest;
import dev.demo.sisepp.tareas.application.dto.TaskResponse;
import dev.demo.sisepp.tareas.application.dto.TaskWithProgressResponse;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    List<TaskResponse> list(String requesterEmail, UUID projectId, UUID activityId,
                            TaskStatus status, String responsible, LocalDate from, LocalDate to);
    List<TaskWithProgressResponse> listWithProgress(UUID activityId);
    List<TaskWithProgressResponse> listWithProgress(UUID activityId, UUID projectId);
    TaskResponse create(CreateTaskRequest request, String requesterEmail);
    TaskResponse reschedule(UUID taskId, RescheduleTaskRequest request, String requesterEmail);
    void delete(UUID taskId);
}
