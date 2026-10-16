import type { ApiError } from '../types';

export function getApiErrorMessage(error: unknown, fallback: string): string {
  const axiosErr = error as { response?: { data?: ApiError | { message?: string } } };
  const data = axiosErr?.response?.data;
  if (data && typeof data === 'object' && 'message' in data && typeof data.message === 'string') {
    return data.message;
  }
  return fallback;
}
