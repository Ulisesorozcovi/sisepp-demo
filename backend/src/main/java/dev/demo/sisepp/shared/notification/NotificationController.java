package dev.demo.sisepp.shared.notification;

import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationJpaRepository repository;
    private final UserRepositoryPort userRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public List<Notification> list(Authentication auth) {
        User user = currentUser(auth);
        return repository.findByUserIdOrderByCreatedAtDesc(user.id())
                .stream().map(NotificationJpaEntity::toDomain).toList();
    }

    @GetMapping("/unread-count")
    @Transactional(readOnly = true)
    public Map<String, Long> unreadCount(Authentication auth) {
        User user = currentUser(auth);
        return Map.of("count", repository.countUnreadByUserId(user.id()));
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        NotificationJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        entity.setRead(true);
        repository.save(entity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mark-all-read")
    @Transactional
    public ResponseEntity<Void> markAllAsRead(Authentication auth) {
        User user = currentUser(auth);
        repository.markAllAsReadByUserId(user.id());
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
