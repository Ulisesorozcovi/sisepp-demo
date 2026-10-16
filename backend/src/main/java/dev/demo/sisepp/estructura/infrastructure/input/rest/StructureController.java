package dev.demo.sisepp.estructura.infrastructure.input.rest;

import dev.demo.sisepp.estructura.application.dto.StructureTreeResponse.StrategicLineNode;
import dev.demo.sisepp.estructura.application.dto.WeightSummaryResponse;
import dev.demo.sisepp.estructura.application.port.input.StructureUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structure")
@RequiredArgsConstructor
public class StructureController {

    private final StructureUseCase structureService;

    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<StrategicLineNode>> tree(@RequestParam(required = false) Integer vigencia) {
        return ResponseEntity.ok(structureService.buildTree(vigencia));
    }

    @GetMapping("/programs/{programId}/weight-summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WeightSummaryResponse> programWeightSummary(@PathVariable UUID programId) {
        return ResponseEntity.ok(structureService.programWeightSummary(programId));
    }

    @GetMapping("/projects/{projectId}/weight-summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WeightSummaryResponse> projectWeightSummary(@PathVariable UUID projectId) {
        return ResponseEntity.ok(structureService.projectWeightSummary(projectId));
    }
}
