package dev.demo.sisepp.estructura.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WeightSummaryResponse(
    UUID parentId,
    String parentName,
    BigDecimal totalWeight,
    BigDecimal remainingWeight,
    boolean isComplete,
    List<WeightItemResponse> children
) {
    public record WeightItemResponse(UUID id, String name, BigDecimal weight) {}

    private static final BigDecimal MAX_WEIGHT = new BigDecimal("100");

    public static WeightSummaryResponse of(UUID parentId, String parentName, BigDecimal totalWeight, List<WeightItemResponse> children) {
        BigDecimal remaining = MAX_WEIGHT.subtract(totalWeight).max(BigDecimal.ZERO);
        boolean complete = totalWeight.compareTo(MAX_WEIGHT) == 0;
        return new WeightSummaryResponse(parentId, parentName, totalWeight, remaining, complete, children);
    }
}
