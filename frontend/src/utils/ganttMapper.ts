import type { Task as GanttTask } from 'gantt-task-react';
import type { ActivityTaskGroup, TaskDataMap } from '../types/gantt';

interface TaskColors {
  backgroundColor: string;
  backgroundSelectedColor: string;
  progressColor: string;
  progressSelectedColor: string;
}

function getTaskColors(
  status: string | undefined,
  dueDate: string,
): TaskColors {
  const normalizedStatus = (status || '').toUpperCase();
  const isOverdue =
    new Date(dueDate) < new Date() &&
    normalizedStatus !== 'COMPLETADA' &&
    normalizedStatus !== 'CUMPLIDA';

  if (normalizedStatus === 'COMPLETADA' || normalizedStatus === 'CUMPLIDA') {
    return {
      backgroundColor: '#bbf7d0',
      backgroundSelectedColor: '#86efac',
      progressColor: '#16a34a',
      progressSelectedColor: '#15803d',
    };
  }

  if (isOverdue || normalizedStatus === 'REZAGADA' || normalizedStatus === 'RETRASADA') {
    return {
      backgroundColor: '#fecaca',
      backgroundSelectedColor: '#fca5a5',
      progressColor: '#dc2626',
      progressSelectedColor: '#b91c1c',
    };
  }

  if (
    normalizedStatus === 'EN_PROCESO' ||
    normalizedStatus === 'EN_EJECUCION'
  ) {
    return {
      backgroundColor: '#99f6e4',
      backgroundSelectedColor: '#5eead4',
      progressColor: '#0d9488',
      progressSelectedColor: '#0f766e',
    };
  }

  return {
    backgroundColor: '#e0e7ff',
    backgroundSelectedColor: '#c7d2fe',
    progressColor: '#6366f1',
    progressSelectedColor: '#4f46e5',
  };
}

export function mapToGanttTasks(
  groups: ActivityTaskGroup[],
  disableDrag = false,
): { ganttTasks: GanttTask[]; taskDataMap: TaskDataMap } {
  const ganttTasks: GanttTask[] = [];
  const taskDataMap: TaskDataMap = new Map();

  groups.forEach((group) => {
    const actTasks = group.tasks;

    const startDates = actTasks
      .map((t) => (t.startDate ? new Date(t.startDate) : new Date(t.dueDate ?? new Date())))
      .filter((d) => !isNaN(d.getTime()));
    const endDates = actTasks
      .map((t) => new Date(t.dueDate ?? new Date()))
      .filter((d) => !isNaN(d.getTime()));

    const actStart =
      startDates.length > 0
        ? new Date(Math.min(...startDates.map((d) => d.getTime())))
        : new Date();
    const actEnd =
      endDates.length > 0
        ? new Date(Math.max(...endDates.map((d) => d.getTime())))
        : new Date();

    const avgProgress =
      actTasks.length > 0
        ? actTasks.reduce(
            (sum, t) => sum + (t.completionPercent || 0),
            0,
          ) / actTasks.length
        : 0;

    ganttTasks.push({
      start: actStart,
      end: actEnd,
      name: group.activityName,
      id: `activity-${group.activityId}`,
      type: 'project',
      progress: Math.min(avgProgress, 100),
      hideChildren: false,
      styles: {
        backgroundColor: '#e2e8f0',
        backgroundSelectedColor: '#cbd5e1',
        progressColor: '#64748b',
        progressSelectedColor: '#475569',
      },
    });

    actTasks.forEach((task) => {
      taskDataMap.set(task.id, task);

      const taskStart = task.startDate
        ? new Date(task.startDate)
        : new Date(task.dueDate ?? new Date());
      const taskEnd = new Date(task.dueDate ?? new Date());

      const effectiveStatus = task.lastFollowUpStatus || task.status;
      const colors = getTaskColors(
        effectiveStatus,
        task.dueDate ?? new Date().toISOString(),
      );

      const normalized = (effectiveStatus || '').toUpperCase();
      const isCompleted =
        normalized === 'COMPLETADA' || normalized === 'CUMPLIDA';

      ganttTasks.push({
        start: taskStart,
        end: taskEnd,
        name: task.name,
        id: task.id,
        type: 'task',
        progress: Math.min(task.completionPercent || 0, 100),
        project: `activity-${task.activityId}`,
        dependencies: [],
        isDisabled: disableDrag || isCompleted,
        styles: colors,
      });
    });
  });

  return { ganttTasks, taskDataMap };
}
