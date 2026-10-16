package dev.demo.sisepp.proyectos.domain.port.output;

import dev.demo.sisepp.proyectos.domain.model.Project;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findByProgramId(UUID programId);
    List<Project> findByVigencia(Integer vigencia);
    List<Project> findByCoordinatorId(UUID userId);
    List<Project> findAll();
    void deleteById(UUID id);
    void addCoordinator(UUID projectId, UUID userId);
    void removeCoordinator(UUID projectId, UUID userId);
    BigDecimal sumWeightsByProgramId(UUID programId, UUID excludeId);
    List<Project> findByPacId(UUID pacId);
    List<Project> findByVigenciaBetween(int startYear, int endYear);
}
