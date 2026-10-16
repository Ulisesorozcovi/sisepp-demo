package dev.demo.sisepp.estructura.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PacMatrixResponse(
    PacInfo pac,
    List<Integer> years,
    List<StrategicLineReport> strategicLines,
    Map<Integer, BigDecimal> grandTotal
) {
    public record PacInfo(String name, int startYear, int endYear, String approvalRef) {}

    public record StrategicLineReport(
        UUID id, String name, BigDecimal weight,
        List<ProgramReport> programs
    ) {}

    public record ProgramReport(
        UUID id, String code, String name, BigDecimal weight,
        List<ProjectReport> projects
    ) {}

    public record ProjectReport(
        UUID id, String name, BigDecimal weight,
        String pip, String objective, BigDecimal objectiveWeight,
        List<ActivityReport> activities,
        Map<Integer, BigDecimal> subtotalBudgets
    ) {}

    public record ActivityReport(
        UUID id, String name, BigDecimal weight,
        String indicator, String indicatorType, String category,
        String measurementUnit,
        Map<Integer, BigDecimal> goals,
        Map<Integer, BigDecimal> budgets
    ) {}
}
