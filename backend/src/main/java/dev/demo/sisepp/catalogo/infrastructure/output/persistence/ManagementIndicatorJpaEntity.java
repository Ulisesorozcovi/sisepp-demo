package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.domain.model.IndicatorType;
import dev.demo.sisepp.shared.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "management_indicators")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManagementIndicatorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20)
    private String code;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "regulation_ref", length = 100)
    private String regulationRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "indicator_type", length = 30)
    private IndicatorType indicatorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecordStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = RecordStatus.ACTIVO;
        if (indicatorType == null) indicatorType = IndicatorType.IMG;
    }
}
