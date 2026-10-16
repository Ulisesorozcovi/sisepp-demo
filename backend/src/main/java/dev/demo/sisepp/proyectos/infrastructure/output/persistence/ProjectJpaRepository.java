package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {
    List<ProjectJpaEntity> findByProgramId(UUID programId);
    List<ProjectJpaEntity> findByVigencia(Integer vigencia);
    List<ProjectJpaEntity> findByCoordinatorEntries_UserId(UUID userId);
    List<ProjectJpaEntity> findByPacId(UUID pacId);
    List<ProjectJpaEntity> findByVigenciaBetween(int startYear, int endYear);

    @Query("SELECT COALESCE(SUM(p.weight), 0) FROM ProjectJpaEntity p WHERE p.program.id = :programId AND (:excludeId IS NULL OR p.id <> :excludeId)")
    BigDecimal sumWeightsByProgramId(@Param("programId") UUID programId, @Param("excludeId") UUID excludeId);
}
