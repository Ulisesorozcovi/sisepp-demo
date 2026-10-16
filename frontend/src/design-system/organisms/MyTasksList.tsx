import { useEffect, useState } from 'react';
import { taskApi } from '../../api/taskApi';
import { followUpApi } from '../../api/followUpApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { Button } from '../atoms/Button';
import { StatusBadge } from '../atoms/StatusBadge';
import { FollowUpHistoryItem } from '../molecules/FollowUpHistoryItem';
import type { Task, FollowUp } from '../../types';

interface Props {
  refreshKey: number;
  onRegisterFollowUp: (task: Task) => void;
}

export function MyTasksList({ refreshKey, onRegisterFollowUp }: Props) {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedTaskId, setExpandedTaskId] = useState<string | null>(null);
  const [followUps, setFollowUps] = useState<Record<string, FollowUp[]>>({});
  const [loadingFU, setLoadingFU] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    taskApi
      .list({})
      .then((data) => active && setTasks(data))
      .catch((err) => active && setError(getApiErrorMessage(err, 'No se pudieron cargar las tareas')))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [refreshKey]);

  function toggleHistory(taskId: string) {
    if (expandedTaskId === taskId) {
      setExpandedTaskId(null);
      return;
    }
    setExpandedTaskId(taskId);
    if (!followUps[taskId]) {
      setLoadingFU(taskId);
      followUpApi
        .list({ taskId })
        .then((data) => setFollowUps((prev) => ({ ...prev, [taskId]: data })))
        .catch(() => setFollowUps((prev) => ({ ...prev, [taskId]: [] })))
        .finally(() => setLoadingFU(null));
    }
  }

  if (loading) return <div className="empty-state">Cargando tareas...</div>;
  if (error) return <div className="empty-state">{error}</div>;
  if (tasks.length === 0) return <div className="empty-state">No tienes tareas asignadas.</div>;

  return (
    <div className="my-tasks-list">
      {tasks.map((task) => (
        <div key={task.id} className="my-task-card">
          <div className="my-task-card-header">
            <div className="my-task-card-info">
              <span className="my-task-card-name">{task.name}</span>
              <span className="my-task-card-meta">
                Meta: {task.physicalGoalValue} {task.physicalGoalUnit} · Fecha límite: {task.dueDate}
                {task.startDate && <> · Inicio: {task.startDate}</>}
              </span>
            </div>
            <div className="my-task-card-actions">
              <StatusBadge status={task.status} />
              <Button variant="ghost" onClick={() => onRegisterFollowUp(task)}>
                Registrar avance
              </Button>
            </div>
          </div>

          <button className="my-task-toggle-history" onClick={() => toggleHistory(task.id)}>
            {expandedTaskId === task.id ? '▾ Ocultar historial' : '▸ Ver historial de seguimiento'}
          </button>

          {expandedTaskId === task.id && (
            <div className="my-task-history">
              {loadingFU === task.id && <div className="empty-state" style={{ padding: 16 }}>Cargando...</div>}
              {followUps[task.id]?.length === 0 && loadingFU !== task.id && (
                <div className="empty-state" style={{ padding: 16 }}>Sin seguimientos registrados.</div>
              )}
              {followUps[task.id]?.map((fu) => (
                <FollowUpHistoryItem key={fu.id} followUp={fu} />
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
