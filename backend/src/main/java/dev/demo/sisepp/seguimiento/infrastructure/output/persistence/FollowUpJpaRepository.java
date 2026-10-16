package dev.demo.sisepp.seguimiento.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowUpJpaRepository extends JpaRepository<FollowUpJpaEntity, UUID> {

    List<FollowUpJpaEntity> findByTaskId(UUID taskId);

    List<FollowUpJpaEntity> findByReportedYearAndReportedMonth(Integer year, Integer month);

    List<FollowUpJpaEntity> findByReportedBy_Id(UUID userId);

    List<FollowUpJpaEntity> findByTask_IdIn(List<UUID> taskIds);

    boolean existsByTask_IdAndReportedYearAndReportedMonth(UUID taskId, int year, int month);
}
