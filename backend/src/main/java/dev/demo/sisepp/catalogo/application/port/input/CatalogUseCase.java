package dev.demo.sisepp.catalogo.application.port.input;

import dev.demo.sisepp.catalogo.application.dto.*;

import java.util.List;
import java.util.UUID;

public interface CatalogUseCase {

    List<MeasurementUnitResponse> listMeasurementUnits();
    MeasurementUnitResponse createMeasurementUnit(CreateMeasurementUnitRequest request);
    MeasurementUnitResponse updateMeasurementUnit(UUID id, UpdateMeasurementUnitRequest request);
    MeasurementUnitResponse inactivateMeasurementUnit(UUID id);

    List<PublicInvestmentProgramResponse> listPublicInvestmentPrograms();

    List<ThematicCategoryResponse> listThematicCategories();

    List<ManagementIndicatorResponse> listManagementIndicators();
}
