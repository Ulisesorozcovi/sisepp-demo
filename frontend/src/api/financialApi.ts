import { axiosClient } from './axiosClient';
import type {
  FinancialExecution,
  CreateFinancialExecutionRequest,
  UpdateFinancialExecutionRequest,
} from '../types';

export const financialApi = {
  list: (params?: { projectId?: string; year?: number; month?: number }) =>
    axiosClient.get<FinancialExecution[]>('/financial-executions', { params }).then((r) => r.data),

  create: (data: CreateFinancialExecutionRequest) =>
    axiosClient.post<FinancialExecution>('/financial-executions', data).then((r) => r.data),

  update: (id: string, data: UpdateFinancialExecutionRequest) =>
    axiosClient.put<FinancialExecution>(`/financial-executions/${id}`, data).then((r) => r.data),

  remove: (id: string) =>
    axiosClient.delete(`/financial-executions/${id}`),
};
