package dev.demo.sisepp.financiero.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FinancialExecutionJpaRepository extends JpaRepository<FinancialExecutionJpaEntity, UUID> {

    List<FinancialExecutionJpaEntity> findByProjectId(UUID projectId);

    List<FinancialExecutionJpaEntity> findByPeriodYearAndPeriodMonth(Integer year, Integer month);
}
