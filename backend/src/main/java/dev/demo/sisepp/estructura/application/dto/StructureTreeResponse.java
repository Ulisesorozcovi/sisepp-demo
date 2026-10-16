package dev.demo.sisepp.estructura.application.dto;

import dev.demo.sisepp.catalogo.application.dto.SdgIndicatorResponse;
import dev.demo.sisepp.shared.RecordStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class StructureTreeResponse {

    public record ActivityNode(UUID id, String name, String code, String measurementUnit,
                               BigDecimal goal, BigDecimal weight, RecordStatus status,
                               List<SdgIndicatorResponse> sdgIndicators) {}

    public record ProjectNode(
        UUID id, String name, String code, String subdivision, BigDecimal weight,
        Integer vigencia, RecordStatus status,
        List<String> coordinatorNames, List<ActivityNode> activities
    ) {}

    public record ProgramNode(UUID id, String name, String code, BigDecimal weight,
                              String description, RecordStatus status, List<ProjectNode> projects) {}

    public record StrategicLineNode(UUID id, String name, String description, RecordStatus status,
                                    List<ProgramNode> programs) {}
}
