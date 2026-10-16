package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sdg_indicators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SdgIndicatorJpaEntity {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
