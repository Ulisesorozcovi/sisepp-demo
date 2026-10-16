import { useEffect, useState } from 'react';
import { taskApi } from '../../api/taskApi';
import { useAuth } from '../../context/AuthContext';
import { getApiErrorMessage } from '../../utils/apiError';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { StatusBadge } from '../atoms/StatusBadge';
import type { Task, TaskStatus } from '../../types';

interface Props {
  refreshKey: number;
  onReschedule: (task: Task) => void;
}

const STATUS_OPTIONS: { value: TaskStatus; label: string }[] = [
  { value: 'PROGRAMADA', label: 'Programada' },
  { value: 'EN_EJECUCION', label: 'En ejecución' },
  { value: 'RETRASADA', label: 'Retrasada' },
  { value: 'COMPLETADA', label: 'Completada' },
  { value: 'NO_APLICA', label: 'No aplica' },
  { value: 'CANCELADA', label: 'Cancelada' },
];

export function TasksTable({ refreshKey, onReschedule }: Props) {
  const { hasRole } = useAuth();
  const canDelete = hasRole('STAFF');
  const [tasks, setTasks] = useState<Task[]>([]);
  const [status, setStatus] = useState<TaskStatus | ''>('');
  const [responsible, setResponsible] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [internalRefresh, setInternalRefresh] = useState(0);

  useEffect(() => {
    let active = true;
    setLoading(true);
    taskApi
      .list({ status: status || undefined, responsible: responsible || undefined })
      .then((data) => active && setTasks(data))
      .catch((err) => active && setError(getApiErrorMessage(err, 'No se pudo cargar el listado de tareas')))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [status, responsible, refreshKey, internalRefresh]);

  async function handleDelete(id: string) {
    if (!window.confirm('¿Seguro que desea eliminar esta tarea?')) return;
    try {
      await taskApi.delete(id);
      setInternalRefresh((k) => k + 1);
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo eliminar la tarea'));
    }
  }

  return (
    <div className="organism-table-card">
      <div className="task-filters">
        <Input placeholder="Buscar por responsable" value={responsible} onChange={(e) => setResponsible(e.target.value)} />
        <Select value={status} onChange={(e) => setStatus(e.target.value as TaskStatus | '')}>
          <option value="">Todos los estados</option>
          {STATUS_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </Select>
      </div>

      {loading && <div className="empty-state">Cargando tareas...</div>}
      {error && <div className="empty-state">{error}</div>}

      {!loading && !error && (
        <table className="users-table tasks-table">
          <thead>
            <tr>
              <th>Tarea</th>
              <th>Meta física</th>
              <th>Inicio</th>
              <th>Fecha límite</th>
              <th>Responsable</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {tasks.length === 0 && (
              <tr>
                <td colSpan={7} className="empty-state">No hay tareas que coincidan con los filtros.</td>
              </tr>
            )}
            {tasks.map((t) => (
              <tr key={t.id}>
                <td style={{ fontWeight: 500 }}>{t.name}</td>
                <td>{t.physicalGoalValue} {t.physicalGoalUnit}</td>
                <td>{t.startDate ?? '—'}</td>
                <td>{t.dueDate}</td>
                <td>{t.responsibleName}</td>
                <td><StatusBadge status={t.status} /></td>
                <td>
                  <div className="row-actions">
                    <button onClick={() => onReschedule(t)}>Reprogramar</button>
                    {canDelete && (
                      <button className="danger" onClick={() => handleDelete(t.id)}>Eliminar</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
