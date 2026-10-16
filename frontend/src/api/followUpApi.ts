import { axiosClient } from './axiosClient';
import type { FollowUp, CreateFollowUpRequest, UpdateFollowUpRequest } from '../types';

export const followUpApi = {
  list: (params?: { taskId?: string; year?: number; month?: number }) =>
    axiosClient.get<FollowUp[]>('/follow-ups', { params }).then((r) => r.data),

  create: (data: CreateFollowUpRequest) =>
    axiosClient.post<FollowUp>('/follow-ups', data).then((r) => r.data),

  update: (id: string, data: UpdateFollowUpRequest) =>
    axiosClient.put<FollowUp>(`/follow-ups/${id}`, data).then((r) => r.data),

  remove: (id: string) =>
    axiosClient.delete(`/follow-ups/${id}`),
};
