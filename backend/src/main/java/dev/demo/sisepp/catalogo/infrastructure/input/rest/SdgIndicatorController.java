package dev.demo.sisepp.catalogo.infrastructure.input.rest;

import dev.demo.sisepp.catalogo.application.dto.SdgIndicatorResponse;
import dev.demo.sisepp.catalogo.application.port.input.SdgIndicatorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/structure/sdg-indicators")
@RequiredArgsConstructor
public class SdgIndicatorController {

    private final SdgIndicatorUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<SdgIndicatorResponse>> list() {
        return ResponseEntity.ok(service.list());
    }
}
