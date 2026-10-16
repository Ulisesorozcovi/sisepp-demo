import { useEffect, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { useAuth } from '../context/AuthContext';
import { Label } from '../design-system/atoms/Label';
import { Select } from '../design-system/atoms/Select';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { GanttView } from '../design-system/organisms/GanttView';
import type { Project, Activity, Task } from '../types';

const STATUS_LABELS: Record<string, string> = {
  PROGRAMADA: 'Programada',
  EN_EJECUCION: 'En ejecución',
  RETRASADA: 'Retrasada',
  COMPLETADA: 'Completada',
  NO_APLICA: 'No aplica',
  CANCELADA: 'Cancelada',
};

type ScheduleView = 'table' | 'gantt';

export function SchedulePage() {
  const { user } = useAuth();
  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState('');
  const [view, setView] = useState<ScheduleView>('table');

  useEffect(() => {
    const loadProjects = user?.role === 'COORDINADOR'
      ? projectApi.listMine()
      : projectApi.list({});
    loadProjects.then(setProjects).catch(() => setProjects([]));
  }, [user?.role]);

  useEffect(() => {
    if (!projectId) { setActivities([]); setActivityId(''); return; }
    activityApi.list(projectId).then(setActivities).catch(() => setActivities([]));
    setActivityId('');
  }, [projectId]);

  useEffect(() => {
    if (!activityId) { setTasks([]); return; }
    taskApi.list({ activityId }).then(setTasks).catch(() => setTasks([]));
  }, [activityId]);

  function formatDate(d: string | null) {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  return (
    <DashboardLayout>
      <div className="page-header">
        <div className="page-header-icon page-header-icon--green">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        <div>
          <h1>Cronograma de Tareas</h1>
          <p>Consulte y gestione los plazos cronol&oacute;gicos y responsables de cada tarea programada.</p>
        </div>
        {user && ['STAFF', 'DIRECTOR', 'COORDINADOR'].includes(user.role) && (
          <div className="view-toggle">
            <button
              className={`view-toggle__btn ${view === 'table' ? 'view-toggle__btn--active' : ''}`}
              onClick={() => setView('table')}
            >
              Tabla
            </button>
            <button
              className={`view-toggle__btn ${view === 'gantt' ? 'view-toggle__btn--active' : ''}`}
              onClick={() => setView('gantt')}
            >
              Gantt
            </button>
          </div>
        )}
      </div>

      <div className="form-card" style={{ marginBottom: 20 }}>
        <div className="form-row form-row--2">
          <div className="molecule-field">
            <Label htmlFor="sch-project">Proyecto</Label>
            <Select id="sch-project" value={projectId} onChange={(e) => setProjectId(e.target.value)}>
              <option value="">— Todos los Proyectos —</option>
              {projects.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
            </Select>
          </div>
          <div className="molecule-field">
            <Label htmlFor="sch-activity">Actividad</Label>
            <Select id="sch-activity" value={activityId} onChange={(e) => setActivityId(e.target.value)} disabled={!projectId}>
              <option value="">{projectId ? '— Seleccione Actividad —' : '— Seleccione Proyecto —'}</option>
              {activities.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
            </Select>
          </div>
        </div>
      </div>

      {view === 'gantt' ? (
        projectId ? (
          <GanttView
            projectId={projectId}
            activityId={activityId || undefined}
            userRole={user?.role ?? 'TECNICO'}
          />
        ) : (
          <div className="form-card">
            <p className="empty-state">Seleccione un proyecto para ver el diagrama de Gantt.</p>
          </div>
        )
      ) : (
        <>
          {activityId && (
            <div className="form-card">
              <h2 className="form-card-title">Tareas programadas</h2>
              {tasks.length === 0 ? (
                <p className="empty-state">No hay tareas programadas para esta actividad.</p>
              ) : (
                <div className="schedule-table-wrap">
                  <table className="schedule-table">
                    <thead>
                      <tr>
                        <th>#</th>
                        <th>Tarea</th>
                        <th>Responsable</th>
                        <th>Meta</th>
                        <th>Inicio</th>
                        <th>Fin</th>
                        <th>Estado</th>
                      </tr>
                    </thead>
                    <tbody>
                      {tasks.map((t, i) => (
                        <tr key={t.id}>
                          <td className="schedule-num">{i + 1}</td>
                          <td>
                            <span className="schedule-task-name">{t.name}</span>
                            {t.observation && <span className="schedule-task-obs">{t.observation}</span>}
                          </td>
                          <td>{t.responsibleName}</td>
                          <td className="schedule-goal">{t.physicalGoalValue} {t.physicalGoalUnit}</td>
                          <td>{formatDate(t.startDate)}</td>
                          <td>{formatDate(t.dueDate)}</td>
                          <td>
                            <span className={`task-status-pill task-status-pill--${t.status.toLowerCase()}`}>
                              {STATUS_LABELS[t.status] ?? t.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {!activityId && (
            <div className="form-card">
              <p className="empty-state">Seleccione un proyecto y actividad para ver el cronograma de tareas.</p>
            </div>
          )}
        </>
      )}
    </DashboardLayout>
  );
}
