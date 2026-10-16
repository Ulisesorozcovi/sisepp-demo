import { axiosClient } from './axiosClient';
import type { Task, TaskStatus, CreateTaskRequest, TaskWithProgress } from '../types';

export interface TaskFilters {
  projectId?: string;
  activityId?: string;
  status?: TaskStatus | '';
  responsible?: string;
  from?: string;
  to?: string;
}

export const taskApi = {
  list: (filters: TaskFilters) =>
    axiosClient.get<Task[]>('/tasks', { params: filters }).then((r) => r.data),

  create: (payload: CreateTaskRequest) =>
    axiosClient.post<Task>('/tasks', payload).then((r) => r.data),

  reschedule: (id: string, newDueDate: string, justification: string) =>
    axiosClient.patch<Task>(`/tasks/${id}/reschedule`, { newDueDate, justification }).then((r) => r.data),

  delete: (id: string) =>
    axiosClient.delete(`/tasks/${id}`).then(() => {}),

  listWithProgress: (activityId?: string, projectId?: string) =>
    axiosClient.get<TaskWithProgress[]>('/tasks/with-progress', { params: { activityId, projectId } }).then((r) => r.data),
};
