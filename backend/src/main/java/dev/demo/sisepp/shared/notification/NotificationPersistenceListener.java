package dev.demo.sisepp.shared.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceListener {

    private final NotificationJpaRepository repository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @EventListener
    @Transactional
    public void onCoordinatorAssigned(CoordinatorAssignedEvent event) {
        Notification notification = new Notification(
                null,
                event.userId(),
                "COORDINATOR_ASSIGNED",
                "Asignación a proyecto",
                "Has sido asignado/a como coordinador/a del proyecto: " + event.projectName(),
                event.projectId(),
                false,
                null
        );
        repository.save(NotificationJpaEntity.fromDomain(notification));
    }

    @EventListener
    @Transactional
    public void onTaskAssigned(TaskAssignedEvent event) {
        String dueFormatted = event.dueDate() != null ? event.dueDate().format(DATE_FMT) : "sin fecha";
        Notification notification = new Notification(
                null,
                event.responsibleUserId(),
                "TASK_ASSIGNED",
                "Nueva tarea asignada",
                event.assignedByName() + " te asignó la tarea: " + event.taskName()
                        + " (fecha límite: " + dueFormatted + ")",
                event.taskId(),
                false,
                null
        );
        repository.save(NotificationJpaEntity.fromDomain(notification));
    }
}
