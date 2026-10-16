package dev.demo.sisepp.catalogo.infrastructure.input.rest;

import dev.demo.sisepp.catalogo.application.dto.*;
import dev.demo.sisepp.catalogo.application.port.input.CatalogUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalogService;

    @GetMapping("/measurement-units")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeasurementUnitResponse>> listMeasurementUnits() {
        return ResponseEntity.ok(catalogService.listMeasurementUnits());
    }

    @PostMapping("/measurement-units")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<MeasurementUnitResponse> createMeasurementUnit(@Valid @RequestBody CreateMeasurementUnitRequest request) {
        return ResponseEntity.status(201).body(catalogService.createMeasurementUnit(request));
    }

    @PutMapping("/measurement-units/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<MeasurementUnitResponse> updateMeasurementUnit(@PathVariable UUID id, @Valid @RequestBody UpdateMeasurementUnitRequest request) {
        return ResponseEntity.ok(catalogService.updateMeasurementUnit(id, request));
    }

    @PatchMapping("/measurement-units/{id}/inactivate")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<MeasurementUnitResponse> inactivateMeasurementUnit(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.inactivateMeasurementUnit(id));
    }

    @GetMapping("/public-investment-programs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PublicInvestmentProgramResponse>> listPublicInvestmentPrograms() {
        return ResponseEntity.ok(catalogService.listPublicInvestmentPrograms());
    }

    @GetMapping("/thematic-categories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ThematicCategoryResponse>> listThematicCategories() {
        return ResponseEntity.ok(catalogService.listThematicCategories());
    }

    @GetMapping("/management-indicators")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ManagementIndicatorResponse>> listManagementIndicators() {
        return ResponseEntity.ok(catalogService.listManagementIndicators());
    }
}
