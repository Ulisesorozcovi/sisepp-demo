import { axiosClient } from './axiosClient';
import type { LoginResponse } from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    axiosClient.post<LoginResponse>('/auth/login', { email, password }).then((r) => r.data),

  forgotPassword: (email: string) =>
    axiosClient.post('/auth/forgot-password', { email }).then((r) => r.data),

  resetPassword: (token: string, newPassword: string) =>
    axiosClient.post('/auth/reset-password', { token, newPassword }).then((r) => r.data),
};
