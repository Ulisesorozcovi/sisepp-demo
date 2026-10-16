package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProjectCoordinatorId implements Serializable {

    private UUID projectId;
    private UUID userId;

    public ProjectCoordinatorId() {}

    public ProjectCoordinatorId(UUID projectId, UUID userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectCoordinatorId that = (ProjectCoordinatorId) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, userId);
    }
}
