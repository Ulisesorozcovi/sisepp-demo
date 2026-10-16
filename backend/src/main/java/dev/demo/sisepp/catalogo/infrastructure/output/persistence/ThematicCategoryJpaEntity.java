package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.shared.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "thematic_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThematicCategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 300)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecordStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); if (status == null) status = RecordStatus.ACTIVO; }
}
