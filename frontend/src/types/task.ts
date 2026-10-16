export type TaskStatus =
  | 'PROGRAMADA'
  | 'EN_EJECUCION'
  | 'RETRASADA'
  | 'COMPLETADA'
  | 'NO_APLICA'
  | 'CANCELADA';

export interface Task {
  id: string;
  name: string;
  activityId: string;
  physicalGoalValue: number;
  physicalGoalUnit: string;
  dueDate: string;
  startDate: string | null;
  responsibleName: string;
  responsibleUserId: string;
  taskType: string | null;
  weight: number | null;
  observation: string | null;
  status: TaskStatus;
  createdAt: string;
}

export interface CreateTaskRequest {
  activityId: string;
  name: string;
  physicalGoalValue: number;
  physicalGoalUnit: string;
  dueDate: string;
  responsibleName: string;
  responsibleUserId: string;
  taskType?: string;
  weight?: number;
  observation?: string;
  startDate?: string;
}

export interface RescheduleTaskRequest {
  newDueDate: string;
  justification: string;
}

export interface TaskRescheduleResponse {
  id: string;
  previousDueDate: string;
  newDueDate: string;
  justification: string;
  changedAt: string;
}

export interface TaskWithProgress {
  id: string;
  name: string;
  responsibleName: string;
  physicalGoalValue: number;
  physicalGoalUnit: string;
  executedAccumulated: number;
  completionPercent: number;
  lastFollowUpStatus: string | null;
  lastFollowUpDate: string | null;
  startDate: string | null;
  dueDate: string | null;
  status: TaskStatus;
  activityId: string;
  activityName: string;
}
