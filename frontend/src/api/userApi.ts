import { axiosClient } from './axiosClient';
import type { UserResponse, CreateUserRequest, UpdateUserRequest } from '../types';

export const userApi = {
  list: (params?: { role?: string; status?: string; search?: string }) =>
    axiosClient.get<UserResponse[]>('/users', { params }).then((r) => r.data),

  listAssignable: () =>
    axiosClient.get<UserResponse[]>('/users/assignable').then((r) => r.data),

  create: (payload: CreateUserRequest) =>
    axiosClient.post<UserResponse>('/users', payload).then((r) => r.data),

  update: (id: string, payload: UpdateUserRequest) =>
    axiosClient.put<UserResponse>(`/users/${id}`, payload).then((r) => r.data),

  inactivate: (id: string) =>
    axiosClient.patch<UserResponse>(`/users/${id}/inactivate`).then((r) => r.data),

  activate: (id: string) =>
    axiosClient.patch<UserResponse>(`/users/${id}/activate`).then((r) => r.data),
};
