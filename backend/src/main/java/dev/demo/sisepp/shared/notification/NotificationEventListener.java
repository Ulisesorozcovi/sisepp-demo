package dev.demo.sisepp.shared.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@ConditionalOnProperty(name = "sisepp.mail.enabled", havingValue = "true")
public class NotificationEventListener {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;
    private final String frontendBaseUrl;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public NotificationEventListener(
            JavaMailSender mailSender,
            @Value("${sisepp.mail.from}") String fromAddress,
            @Value("${sisepp.mail.from-name}") String fromName,
            @Value("${sisepp.frontend.base-url}") String frontendBaseUrl) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Async
    @EventListener
    public void onCoordinatorAssigned(CoordinatorAssignedEvent event) {
        String subject = "SISEPP — Asignación a proyecto: " + event.projectName();
        String body = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #111111; padding: 20px; text-align: center;">
                    <h1 style="color: #ffffff; margin: 0;">SISEPP</h1>
                    <p style="color: #6B7280; margin: 5px 0 0;">Demo de portafolio</p>
                </div>
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #111111;">Asignación a proyecto</h2>
                    <p>Hola <strong>%s</strong>,</p>
                    <p>Has sido asignado/a como coordinador/a del proyecto:</p>
                    <div style="background: #fff; border-left: 4px solid #6B7280; padding: 15px; margin: 20px 0;">
                        <strong>%s</strong>
                    </div>
                    <p>Ingresa al sistema para consultar las actividades y tareas asociadas.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #6B7280; color: #ffffff; padding: 12px 30px;
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Ir a SISEPP
                        </a>
                    </div>
                </div>
                <div style="background-color: #111111; padding: 15px; text-align: center;">
                    <p style="color: #aaa; font-size: 12px; margin: 0;">Corporación para el Desarrollo Sostenible del Urabá</p>
                </div>
            </div>
            """.formatted(event.userFullName(), event.projectName(), frontendBaseUrl);
        send(event.userEmail(), subject, body);
    }

    @Async
    @EventListener
    public void onTaskAssigned(TaskAssignedEvent event) {
        String dueFormatted = event.dueDate() != null ? event.dueDate().format(DATE_FMT) : "Sin fecha";
        String subject = "SISEPP — Nueva tarea asignada: " + event.taskName();
        String body = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #111111; padding: 20px; text-align: center;">
                    <h1 style="color: #ffffff; margin: 0;">SISEPP</h1>
                    <p style="color: #6B7280; margin: 5px 0 0;">Demo de portafolio</p>
                </div>
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #111111;">Nueva tarea asignada</h2>
                    <p>Hola <strong>%s</strong>,</p>
                    <p><strong>%s</strong> te ha asignado una nueva tarea:</p>
                    <div style="background: #fff; border-left: 4px solid #6B7280; padding: 15px; margin: 20px 0;">
                        <strong>%s</strong><br>
                        <span style="color: #666; font-size: 14px;">Fecha límite: %s</span>
                    </div>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #6B7280; color: #ffffff; padding: 12px 30px;
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Ver mis tareas
                        </a>
                    </div>
                </div>
                <div style="background-color: #111111; padding: 15px; text-align: center;">
                    <p style="color: #aaa; font-size: 12px; margin: 0;">Corporación para el Desarrollo Sostenible del Urabá</p>
                </div>
            </div>
            """.formatted(
                event.responsibleFullName(),
                event.assignedByName(),
                event.taskName(),
                dueFormatted,
                frontendBaseUrl + "/mis-tareas"
            );
        send(event.responsibleEmail(), subject, body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[NOTIFICACIÓN] Enviada a {} — {}", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("[NOTIFICACIÓN] Error enviando a {}: {}", to, e.getMessage(), e);
        }
    }
}
