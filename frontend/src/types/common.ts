export type RecordStatus = 'ACTIVO' | 'INACTIVO';

export interface ApiError {
  status: number;
  code: string;
  message: string;
  path: string;
  timestamp: string;
  traceId: string | null;
}
