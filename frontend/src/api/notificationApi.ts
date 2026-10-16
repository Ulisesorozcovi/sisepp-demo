import { axiosClient } from './axiosClient';

export interface AppNotification {
  id: string;
  userId: string;
  type: string;
  title: string;
  message: string;
  referenceId: string | null;
  read: boolean;
  createdAt: string;
}

export const notificationApi = {
  list: () =>
    axiosClient.get<AppNotification[]>('/notifications').then((r) => r.data),

  unreadCount: () =>
    axiosClient.get<{ count: number }>('/notifications/unread-count').then((r) => r.data.count),

  markAsRead: (id: string) =>
    axiosClient.patch(`/notifications/${id}/read`).then((r) => r.data),

  markAllAsRead: () =>
    axiosClient.post('/notifications/mark-all-read').then((r) => r.data),
};
