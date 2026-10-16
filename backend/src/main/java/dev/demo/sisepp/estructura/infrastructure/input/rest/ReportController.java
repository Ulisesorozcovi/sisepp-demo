package dev.demo.sisepp.estructura.infrastructure.input.rest;

import dev.demo.sisepp.estructura.application.dto.DashboardResponse;
import dev.demo.sisepp.estructura.application.dto.PacMatrixResponse;
import dev.demo.sisepp.estructura.application.port.input.DashboardUseCase;
import dev.demo.sisepp.estructura.application.port.input.PacMatrixUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PacMatrixUseCase pacMatrixService;
    private final DashboardUseCase dashboardService;

    @GetMapping("/pac-matrix")
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<PacMatrixResponse> pacMatrix(
        @RequestParam UUID pacId,
        @RequestParam(required = false) Integer vigencia
    ) {
        return ResponseEntity.ok(pacMatrixService.generate(pacId, vigencia));
    }

    /**
     * Resumen del avance físico de la vigencia, recortado al alcance del rol:
     * el director y el staff ven todo el PAC, el coordinador solo sus proyectos
     * asignados y el técnico solo las tareas de las que es responsable.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResponse> dashboard(
        @AuthenticationPrincipal String email,
        @RequestParam(required = false) Integer vigencia
    ) {
        return ResponseEntity.ok(dashboardService.generate(vigencia, email));
    }
}
