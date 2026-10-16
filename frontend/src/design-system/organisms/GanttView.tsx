import { useState, useEffect, useCallback, useMemo } from 'react';
import { Gantt, ViewMode } from 'gantt-task-react';
import type { Task as GanttTask } from 'gantt-task-react';
import 'gantt-task-react/dist/index.css';

import { activityApi } from '../../api/structureApi';
import { taskApi } from '../../api/taskApi';
import type { UserRole } from '../../types/auth';
import type { Activity } from '../../types/structure';
import type { ActivityTaskGroup, TaskDataMap } from '../../types/gantt';
import { mapToGanttTasks } from '../../utils/ganttMapper';

import './GanttView.css';

interface GanttViewProps {
  projectId: string;
  activityId?: string;
  userRole: UserRole;
}

const VIEW_OPTIONS: { mode: ViewMode; label: string }[] = [
  { mode: ViewMode.Day, label: 'Día' },
  { mode: ViewMode.Week, label: 'Semana' },
  { mode: ViewMode.Month, label: 'Mes' },
  { mode: ViewMode.Year, label: 'Año' },
];

const COLUMN_WIDTHS: Record<string, number> = {
  [ViewMode.Day]: 60,
  [ViewMode.Week]: 120,
  [ViewMode.Month]: 200,
  [ViewMode.Year]: 300,
};

export function GanttView({ projectId, activityId, userRole }: GanttViewProps) {
  const [viewMode, setViewMode] = useState<ViewMode>(ViewMode.Month);
  const [ganttTasks, setGanttTasks] = useState<GanttTask[]>([]);
  const [taskDataMap, setTaskDataMap] = useState<TaskDataMap>(new Map());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const canReschedule = userRole === 'STAFF' || userRole === 'COORDINADOR';

  const loadData = useCallback(async () => {
    if (!projectId) return;
    setLoading(true);
    setError(null);

    try {
      const activities: Activity[] = await activityApi.list(projectId);

      const targetActivities = activityId
        ? activities.filter((a) => a.id === activityId)
        : activities;

      const groups: ActivityTaskGroup[] = (
        await Promise.all(
          targetActivities.map(async (act) => {
            try {
              const tasks = await taskApi.listWithProgress(act.id);
              return {
                activityId: act.id,
                activityName: act.name,
                tasks,
              };
            } catch {
              return { activityId: act.id, activityName: act.name, tasks: [] };
            }
          }),
        )
      ).filter((g) => g.tasks.length > 0);

      const { ganttTasks: mapped, taskDataMap: dataMap } =
        mapToGanttTasks(groups, !canReschedule);
      setGanttTasks(mapped);
      setTaskDataMap(dataMap);
    } catch {
      setError('Error al cargar datos del cronograma.');
    } finally {
      setLoading(false);
    }
  }, [projectId, activityId]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleDateChange = useCallback(
    async (task: GanttTask) => {
      if (task.type === 'project') return;
      if (!canReschedule) return;

      try {
        const newDueDate = task.end.toISOString().split('T')[0];
        await taskApi.reschedule(
          task.id,
          newDueDate,
          'Reprogramado desde vista Gantt',
        );
        await loadData();
      } catch {
        await loadData();
      }
    },
    [canReschedule, loadData],
  );

  const handleClick = useCallback((_task: GanttTask) => {
    // Future: open detail panel
  }, []);

  const TaskListHeader = useMemo(
    () =>
      function GanttTableHeader() {
        return (
          <div className="gantt-table-header">
            <div className="gantt-table-header__cell gantt-table-header__cell--name">
              Tarea
            </div>
            <div className="gantt-table-header__cell gantt-table-header__cell--assigned">
              Responsable
            </div>
            <div className="gantt-table-header__cell gantt-table-header__cell--status">
              Avance
            </div>
          </div>
        );
      },
    [],
  );

  const TaskListTable = useMemo(
    () =>
      function GanttTableBody({
        tasks: taskList,
      }: {
        tasks: GanttTask[];
      }) {
        return (
          <div className="gantt-table-body">
            {taskList.map((t) => {
              const isActivity = t.type === 'project';
              const original = taskDataMap.get(t.id);
              const responsible = original?.responsibleName ?? '';

              return (
                <div
                  key={t.id}
                  className={`gantt-table-row ${isActivity ? 'gantt-table-row--activity' : ''}`}
                >
                  <div className="gantt-table-row__cell gantt-table-row__cell--name">
                    {isActivity ? '\u{1F4CC} ' : '  ✓ '}
                    <span title={t.name}>
                      {t.name.length > 40
                        ? t.name.substring(0, 40) + '…'
                        : t.name}
                    </span>
                  </div>
                  <div className="gantt-table-row__cell gantt-table-row__cell--assigned">
                    {responsible}
                  </div>
                  <div className="gantt-table-row__cell gantt-table-row__cell--status">
                    <span
                      className="gantt-status-badge"
                      style={{
                        background:
                          t.styles?.progressColor ?? '#6b7280',
                      }}
                    >
                      {Math.round(t.progress)}%
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        );
      },
    [taskDataMap],
  );

  if (loading) {
    return <div className="gantt-loading">Cargando cronograma…</div>;
  }

  if (error) {
    return <div className="gantt-error">{error}</div>;
  }

  if (ganttTasks.length === 0) {
    return (
      <div className="gantt-empty">
        No hay tareas programadas para este proyecto.
      </div>
    );
  }

  return (
    <div className="gantt-container">
      <div className="gantt-toolbar">
        <span className="gantt-toolbar__label">Vista:</span>
        {VIEW_OPTIONS.map(({ mode, label }) => (
          <button
            key={label}
            className={`gantt-toolbar__btn ${viewMode === mode ? 'gantt-toolbar__btn--active' : ''}`}
            onClick={() => setViewMode(mode)}
          >
            {label}
          </button>
        ))}
      </div>

      <Gantt
        tasks={ganttTasks}
        viewMode={viewMode}
        onDateChange={canReschedule ? handleDateChange : undefined}
        onClick={handleClick}
        listCellWidth="350px"
        columnWidth={COLUMN_WIDTHS[viewMode] ?? 200}
        rowHeight={42}
        barCornerRadius={4}
        fontSize="12px"
        todayColor="rgba(37, 99, 235, 0.06)"
        locale="es"
        TaskListHeader={TaskListHeader}
        TaskListTable={TaskListTable}
      />
    </div>
  );
}
