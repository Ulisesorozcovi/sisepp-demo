package dev.demo.sisepp.shared.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "sisepp.mail.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingNotificationListener {

    @EventListener
    public void onCoordinatorAssigned(CoordinatorAssignedEvent event) {
        log.info("[NOTIFICACIÓN] Coordinador {} asignado al proyecto '{}'",
                event.userEmail(), event.projectName());
    }

    @EventListener
    public void onTaskAssigned(TaskAssignedEvent event) {
        log.info("[NOTIFICACIÓN] Tarea '{}' asignada a {} (por {}), fecha límite: {}",
                event.taskName(), event.responsibleEmail(),
                event.assignedByName(), event.dueDate());
    }
}
