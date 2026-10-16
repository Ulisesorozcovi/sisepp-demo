package dev.demo.sisepp.actividades.domain.port.output;

import dev.demo.sisepp.actividades.domain.model.Activity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepositoryPort {
    Activity save(Activity activity);
    Optional<Activity> findById(UUID id);
    List<Activity> findByProjectId(UUID projectId);
    List<Activity> findByProjectIdIn(List<UUID> projectIds);
    List<Activity> findAll();
    BigDecimal sumWeightsByProjectId(UUID projectId, UUID excludeId);
}
