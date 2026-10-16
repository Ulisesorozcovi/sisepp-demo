import { axiosClient } from './axiosClient';
import type { PacMatrixResponse, DashboardResponse } from '../types';

export const reportApi = {
  getPacMatrix: (params: { pacId: string; vigencia?: number }) =>
    axiosClient.get<PacMatrixResponse>('/reports/pac-matrix', { params }).then((r) => r.data),

  getDashboard: (params?: { vigencia?: number }) =>
    axiosClient.get<DashboardResponse>('/reports/dashboard', { params }).then((r) => r.data),
};
