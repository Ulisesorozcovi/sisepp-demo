import { axiosClient } from './axiosClient';
import type { PlanningInstrument, InstrumentType, InstrumentStatus, CreatePlanningInstrumentRequest, UpdatePlanningInstrumentRequest } from '../types';

export const planningApi = {
  list: (params?: { type?: InstrumentType; status?: InstrumentStatus }) =>
    axiosClient.get<PlanningInstrument[]>('/planning-instruments', { params }).then((r) => r.data),

  getById: (id: string) =>
    axiosClient.get<PlanningInstrument>(`/planning-instruments/${id}`).then((r) => r.data),

  create: (data: CreatePlanningInstrumentRequest) =>
    axiosClient.post<PlanningInstrument>('/planning-instruments', data).then((r) => r.data),

  update: (id: string, data: UpdatePlanningInstrumentRequest) =>
    axiosClient.put<PlanningInstrument>(`/planning-instruments/${id}`, data).then((r) => r.data),
};
