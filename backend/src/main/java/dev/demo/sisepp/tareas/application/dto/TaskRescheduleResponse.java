package dev.demo.sisepp.tareas.application.dto;

import dev.demo.sisepp.tareas.domain.model.TaskReschedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskRescheduleResponse(
    UUID id,
    LocalDate previousDueDate,
    LocalDate newDueDate,
    String justification,
    LocalDateTime changedAt
) {
    public static TaskRescheduleResponse fromDomain(TaskReschedule reschedule) {
        return new TaskRescheduleResponse(
            reschedule.id(),
            reschedule.previousDueDate(),
            reschedule.newDueDate(),
            reschedule.justification(),
            reschedule.changedAt()
        );
    }
}
