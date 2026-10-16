export type FollowUpStatus = 'EN_PROCESO' | 'CUMPLIDA' | 'REZAGADA' | 'NO_APLICA';

export interface FollowUp {
  id: string;
  taskId: string;
  /** Nombre de la tarea, resuelto por el backend. */
  taskName: string | null;
  reportedMonth: number;
  reportedYear: number;
  physicalProgress: number;
  evidenceUrl: string | null;
  attachmentUrl: string | null;
  status: FollowUpStatus;
  observations: string | null;
  reportedByUserId: string;
  reportedByName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFollowUpRequest {
  taskId: string;
  reportedMonth: number;
  reportedYear: number;
  physicalProgress?: number;
  evidenceUrl?: string;
  attachmentUrl?: string;
  status?: FollowUpStatus;
  observations?: string;
}

export interface UpdateFollowUpRequest {
  physicalProgress?: number;
  evidenceUrl?: string;
  attachmentUrl?: string;
  status?: FollowUpStatus;
  observations?: string;
}
