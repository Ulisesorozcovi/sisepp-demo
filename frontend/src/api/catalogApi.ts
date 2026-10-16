import { axiosClient } from './axiosClient';
import type { MeasurementUnit, PublicInvestmentProgram, ThematicCategory, ManagementIndicator } from '../types';

export const catalogApi = {
  listMeasurementUnits: () =>
    axiosClient.get<MeasurementUnit[]>('/catalogs/measurement-units').then((r) => r.data),

  listPublicInvestmentPrograms: () =>
    axiosClient.get<PublicInvestmentProgram[]>('/catalogs/public-investment-programs').then((r) => r.data),

  listThematicCategories: () =>
    axiosClient.get<ThematicCategory[]>('/catalogs/thematic-categories').then((r) => r.data),

  listManagementIndicators: () =>
    axiosClient.get<ManagementIndicator[]>('/catalogs/management-indicators').then((r) => r.data),
};
