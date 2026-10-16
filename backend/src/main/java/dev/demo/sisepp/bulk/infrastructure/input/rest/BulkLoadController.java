package dev.demo.sisepp.bulk.infrastructure.input.rest;

import dev.demo.sisepp.bulk.application.dto.BulkLoadSummary;
import dev.demo.sisepp.bulk.application.dto.BulkPacRequest;
import dev.demo.sisepp.bulk.application.port.input.BulkLoadPacUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bulk")
@RequiredArgsConstructor
public class BulkLoadController {

    private final BulkLoadPacUseCase bulkLoadService;

    @PostMapping("/pac-structure")
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")
    public ResponseEntity<BulkLoadSummary> loadPacStructure(@Valid @RequestBody BulkPacRequest request) {
        return ResponseEntity.ok(bulkLoadService.execute(request));
    }
}
