import { axiosClient } from './axiosClient';
import type {
  Activity,
  AnnualGoalEntry,
  AnnualBudgetEntry,
  Program,
  Project,
  SdgIndicator,
  StrategicLine,
  StrategicLineNode,
} from '../types';

export const strategicLineApi = {
  list: () =>
    axiosClient.get<StrategicLine[]>('/structure/strategic-lines').then((r) => r.data),
  create: (data: { name: string; description?: string }) =>
    axiosClient.post<StrategicLine>('/structure/strategic-lines', data).then((r) => r.data),
  update: (id: string, data: { name: string; description?: string }) =>
    axiosClient.put<StrategicLine>(`/structure/strategic-lines/${id}`, data).then((r) => r.data),
};

export const programApi = {
  list: (strategicLineId?: string) =>
    axiosClient.get<Program[]>('/structure/programs', { params: { strategicLineId } }).then((r) => r.data),
  create: (data: { name: string; strategicLineId: string; code?: string; weight?: number; description?: string }) =>
    axiosClient.post<Program>('/structure/programs', data).then((r) => r.data),
  update: (id: string, data: { name: string; code?: string; weight?: number; description?: string }) =>
    axiosClient.put<Program>(`/structure/programs/${id}`, data).then((r) => r.data),
};

export const projectApi = {
  list: (params?: { programId?: string; vigencia?: number; mine?: boolean }) =>
    axiosClient.get<Project[]>('/structure/projects', { params }).then((r) => r.data),
  listMine: () =>
    axiosClient.get<Project[]>('/structure/projects', { params: { mine: true } }).then((r) => r.data),
  create: (data: { name: string; programId: string; vigencia: number; code?: string; subdivision?: string; weight?: number; objective?: string; objectiveWeight?: number; pipId?: string }) =>
    axiosClient.post<Project>('/structure/projects', data).then((r) => r.data),
  update: (id: string, data: { name: string; vigencia: number; code?: string; subdivision?: string; weight?: number; objective?: string; objectiveWeight?: number; pipId?: string }) =>
    axiosClient.put<Project>(`/structure/projects/${id}`, data).then((r) => r.data),
  assignCoordinator: (projectId: string, data: { userId: string }) =>
    axiosClient.post<Project>(`/structure/projects/${projectId}/coordinators`, data).then((r) => r.data),
  removeCoordinator: (projectId: string, userId: string) =>
    axiosClient.delete<Project>(`/structure/projects/${projectId}/coordinators/${userId}`).then((r) => r.data),
};

export const activityApi = {
  list: (projectId?: string) =>
    axiosClient.get<Activity[]>('/structure/activities', { params: { projectId } }).then((r) => r.data),
  create: (data: { name: string; projectId: string; sdgIndicatorIds: number[]; code?: string; measurementUnit?: string; goal?: number; weight?: number; indicatorDescription?: string; indicatorType?: string; thematicCategoryId?: string; managementIndicatorId?: string; measurementUnitId?: string }) =>
    axiosClient.post<Activity>('/structure/activities', data).then((r) => r.data),
  update: (id: string, data: { name: string; sdgIndicatorIds: number[]; code?: string; measurementUnit?: string; goal?: number; weight?: number; indicatorDescription?: string; indicatorType?: string; thematicCategoryId?: string; managementIndicatorId?: string; measurementUnitId?: string; annualGoals?: AnnualGoalEntry[]; annualBudgets?: AnnualBudgetEntry[] }) =>
    axiosClient.put<Activity>(`/structure/activities/${id}`, data).then((r) => r.data),
  getById: (id: string) =>
    axiosClient.get<Activity>(`/structure/activities/${id}`).then((r) => r.data),
};

export const sdgIndicatorApi = {
  list: () =>
    axiosClient.get<SdgIndicator[]>('/structure/sdg-indicators').then((r) => r.data),
};

export const structureTreeApi = {
  get: (vigencia?: number) =>
    axiosClient.get<StrategicLineNode[]>('/structure/tree', { params: { vigencia } }).then((r) => r.data),
};

export interface WeightItemResponse {
  id: string;
  name: string;
  weight: number;
}

export interface WeightSummaryResponse {
  parentId: string;
  parentName: string;
  totalWeight: number;
  remainingWeight: number;
  isComplete: boolean;
  children: WeightItemResponse[];
}

export const weightSummaryApi = {
  forProgram: (programId: string) =>
    axiosClient.get<WeightSummaryResponse>(`/structure/programs/${programId}/weight-summary`).then((r) => r.data),
  forProject: (projectId: string) =>
    axiosClient.get<WeightSummaryResponse>(`/structure/projects/${projectId}/weight-summary`).then((r) => r.data),
};
