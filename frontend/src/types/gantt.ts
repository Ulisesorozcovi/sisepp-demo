import type { Task as GanttTask } from 'gantt-task-react';
import type { TaskWithProgress } from './task';

/** Agrupación: actividad con sus tareas (datos del backend) */
export interface ActivityTaskGroup {
  activityId: string;
  activityName: string;
  tasks: TaskWithProgress[];
}

/** Mapa para lookup rápido de datos originales */
export type TaskDataMap = Map<string, TaskWithProgress>;

/** Re-export for convenience */
export type { GanttTask };
