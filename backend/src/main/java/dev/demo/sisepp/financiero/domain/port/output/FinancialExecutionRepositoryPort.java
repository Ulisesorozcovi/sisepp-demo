package dev.demo.sisepp.financiero.domain.port.output;

import dev.demo.sisepp.financiero.domain.model.FinancialExecution;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialExecutionRepositoryPort {
    FinancialExecution save(FinancialExecution execution);
    Optional<FinancialExecution> findById(UUID id);
    List<FinancialExecution> findByProjectId(UUID projectId);
    List<FinancialExecution> findByPeriodYearAndPeriodMonth(Integer year, Integer month);
    void deleteById(UUID id);
}
