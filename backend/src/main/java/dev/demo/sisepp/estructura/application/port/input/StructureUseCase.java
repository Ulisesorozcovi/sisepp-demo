package dev.demo.sisepp.estructura.application.port.input;

import dev.demo.sisepp.estructura.application.dto.StructureTreeResponse.StrategicLineNode;
import dev.demo.sisepp.estructura.application.dto.WeightSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface StructureUseCase {
    List<StrategicLineNode> buildTree(Integer vigencia);
    WeightSummaryResponse programWeightSummary(UUID programId);
    WeightSummaryResponse projectWeightSummary(UUID projectId);
}
