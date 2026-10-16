package dev.demo.sisepp.bulk.application.dto;

public record BulkLoadSummary(
    boolean success,
    Summary summary
) {
    public record Summary(
        int strategicLinesCreated,
        int strategicLinesReused,
        int programsCreated,
        int programsReused,
        int projectsCreated,
        int projectsUpdated,
        int activitiesCreated,
        int activitiesUpdated,
        int annualGoalsCreated,
        int annualBudgetsCreated
    ) {}
}
