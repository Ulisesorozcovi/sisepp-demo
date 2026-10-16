package dev.demo.sisepp.catalogo.application.service;

import dev.demo.sisepp.catalogo.application.dto.*;
import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;
import dev.demo.sisepp.catalogo.application.port.input.CatalogUseCase;
import dev.demo.sisepp.catalogo.domain.port.output.ManagementIndicatorRepositoryPort;
import dev.demo.sisepp.catalogo.domain.port.output.MeasurementUnitRepositoryPort;
import dev.demo.sisepp.catalogo.domain.port.output.PublicInvestmentProgramRepositoryPort;
import dev.demo.sisepp.catalogo.domain.port.output.ThematicCategoryRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final MeasurementUnitRepositoryPort measurementUnitRepo;
    private final PublicInvestmentProgramRepositoryPort pipRepo;
    private final ThematicCategoryRepositoryPort thematicCategoryRepo;
    private final ManagementIndicatorRepositoryPort managementIndicatorRepo;

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementUnitResponse> listMeasurementUnits() {
        return measurementUnitRepo.findActive().stream().map(MeasurementUnitResponse::fromDomain).toList();
    }

    @Override
    @Transactional
    public MeasurementUnitResponse createMeasurementUnit(CreateMeasurementUnitRequest request) {
        MeasurementUnit unit = new MeasurementUnit(null, request.code(), request.name(), RecordStatus.ACTIVO, null);
        return MeasurementUnitResponse.fromDomain(measurementUnitRepo.save(unit));
    }

    @Override
    @Transactional
    public MeasurementUnitResponse updateMeasurementUnit(UUID id, UpdateMeasurementUnitRequest request) {
        MeasurementUnit unit = measurementUnitRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada"));
        MeasurementUnit updated = new MeasurementUnit(unit.id(), request.code(), request.name(), unit.status(), unit.createdAt());
        return MeasurementUnitResponse.fromDomain(measurementUnitRepo.save(updated));
    }

    @Override
    @Transactional
    public MeasurementUnitResponse inactivateMeasurementUnit(UUID id) {
        MeasurementUnit unit = measurementUnitRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada"));
        MeasurementUnit inactivated = new MeasurementUnit(unit.id(), unit.code(), unit.name(), RecordStatus.INACTIVO, unit.createdAt());
        return MeasurementUnitResponse.fromDomain(measurementUnitRepo.save(inactivated));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicInvestmentProgramResponse> listPublicInvestmentPrograms() {
        return pipRepo.findActive().stream().map(PublicInvestmentProgramResponse::fromDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThematicCategoryResponse> listThematicCategories() {
        return thematicCategoryRepo.findActive().stream().map(ThematicCategoryResponse::fromDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagementIndicatorResponse> listManagementIndicators() {
        return managementIndicatorRepo.findActive().stream().map(ManagementIndicatorResponse::fromDomain).toList();
    }
}
