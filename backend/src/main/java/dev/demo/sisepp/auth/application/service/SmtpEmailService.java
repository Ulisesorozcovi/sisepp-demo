package dev.demo.sisepp.auth.application.service;

import dev.demo.sisepp.usuarios.domain.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "sisepp.mail.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;
    private final String frontendBaseUrl;

    public SmtpEmailService(
            JavaMailSender mailSender,
            @Value("${sisepp.mail.from}") String fromAddress,
            @Value("${sisepp.mail.from-name}") String fromName,
            @Value("${sisepp.frontend.base-url}") String frontendBaseUrl) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;
        String subject = "Bienvenido a SISEPP - Activa tu cuenta";
        String body = buildActivationBody(user.fullName(), resetLink);
        send(user.email(), subject, body);
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;
        String subject = "SISEPP - Recuperación de contraseña";
        String body = buildPasswordResetBody(user.fullName(), resetLink);
        send(user.email(), subject, body);
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
            log.info("[EMAIL] Enviado a {} — asunto: {}", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("[EMAIL] Error enviando correo a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el correo electrónico", e);
        }
    }

    private String buildPasswordResetBody(String fullName, String resetLink) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #111111; padding: 20px; text-align: center;">
                    <h1 style="color: #ffffff; margin: 0;">SISEPP</h1>
                    <p style="color: #6B7280; margin: 5px 0 0;">Demo de portafolio</p>
                </div>
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #111111;">Recuperación de contraseña</h2>
                    <p>Hola <strong>%s</strong>,</p>
                    <p>Recibimos una solicitud para restablecer tu contraseña. Haz clic en el siguiente enlace:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #6B7280; color: #ffffff; padding: 12px 30px;
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Restablecer contraseña
                        </a>
                    </div>
                    <p style="color: #666; font-size: 14px;">Este enlace expira en 30 minutos. Si no solicitaste este cambio, ignora este correo.</p>
                </div>
                <div style="background-color: #111111; padding: 15px; text-align: center;">
                    <p style="color: #aaa; font-size: 12px; margin: 0;">Corporación para el Desarrollo Sostenible del Urabá</p>
                </div>
            </div>
            """.formatted(fullName, resetLink);
    }

    private String buildActivationBody(String fullName, String resetLink) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #111111; padding: 20px; text-align: center;">
                    <h1 style="color: #ffffff; margin: 0;">SISEPP</h1>
                    <p style="color: #6B7280; margin: 5px 0 0;">Demo de portafolio</p>
                </div>
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #111111;">Bienvenido a SISEPP</h2>
                    <p>Hola <strong>%s</strong>,</p>
                    <p>Se ha creado tu cuenta en el Sistema de Seguimiento a Proyectos y Programas. Para activarla, establece tu contraseña:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #6B7280; color: #ffffff; padding: 12px 30px;
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Activar mi cuenta
                        </a>
                    </div>
                    <p style="color: #666; font-size: 14px;">Este enlace expira en 30 minutos.</p>
                </div>
                <div style="background-color: #111111; padding: 15px; text-align: center;">
                    <p style="color: #aaa; font-size: 12px; margin: 0;">Corporación para el Desarrollo Sostenible del Urabá</p>
                </div>
            </div>
            """.formatted(fullName, resetLink);
    }
}
