import { useEffect, useState, type FormEvent } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { useAuth } from '../context/AuthContext';
import { Label } from '../design-system/atoms/Label';
import { Input } from '../design-system/atoms/Input';
import { Select } from '../design-system/atoms/Select';
import { Button } from '../design-system/atoms/Button';
import { Alert } from '../design-system/molecules/Alert';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { userApi } from '../api/userApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { Project, Activity, Task, UserResponse } from '../types';

export function TaskCatalogPage() {
  const { user } = useAuth();
  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState('');

  const [tecnicos, setTecnicos] = useState<UserResponse[]>([]);
  const [name, setName] = useState('');
  const [goalValue, setGoalValue] = useState('0');
  const [goalUnit, setGoalUnit] = useState('');
  const [weight, setWeight] = useState('');
  const [taskType, setTaskType] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [startDate, setStartDate] = useState('');
  const [responsibleUserId, setResponsibleUserId] = useState('');
  const [observation, setObservation] = useState('');

  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadProjects = user?.role === 'COORDINADOR'
      ? projectApi.listMine()
      : projectApi.list({});
    loadProjects.then(setProjects).catch(() => setProjects([]));
    userApi.listAssignable().then(setTecnicos).catch(() => setTecnicos([]));
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

  function resetForm() {
    setName('');
    setGoalValue('0');
    setGoalUnit('');
    setWeight('');
    setTaskType('');
    setDueDate('');
    setStartDate('');
    setResponsibleUserId('');
    setObservation('');
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!activityId || !name.trim()) return;
    setSaving(true);
    setError(null);
    setSuccess(null);
    try {
      const selectedUser = tecnicos.find((u) => u.id === responsibleUserId);
      await taskApi.create({
        activityId,
        name: name.trim(),
        physicalGoalValue: Number(goalValue) || 0,
        physicalGoalUnit: goalUnit.trim() || 'Unidad',
        dueDate,
        responsibleName: selectedUser?.fullName ?? 'Por asignar',
        responsibleUserId,
        startDate: startDate || undefined,
        taskType: taskType || undefined,
        weight: weight ? Number(weight) : undefined,
        observation: observation.trim() || undefined,
      });
      setSuccess(`Tarea "${name.trim()}" registrada exitosamente.`);
      resetForm();
      const updated = await taskApi.list({ activityId });
      setTasks(updated);
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo registrar la tarea'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <DashboardLayout>
      <div className="page-header">
        <div className="page-header-icon page-header-icon--slate">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2M9 12h6M9 16h6" />
          </svg>
        </div>
        <div>
          <h1>Catálogo de Tareas</h1>
          <p>Registre las tareas técnicas que componen cada actividad del PAC.</p>
        </div>
      </div>

      <div className="form-page-layout">
        <div className="form-card">
          <h2 className="form-card-title">Añadir Nueva Tarea Técnica al Catálogo Base</h2>

          {error && <Alert>{error}</Alert>}
          {success && <div className="alert-success">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-row form-row--2">
              <div className="molecule-field">
                <Label htmlFor="cat-project">Proyecto Asignado</Label>
                <Select id="cat-project" value={projectId} onChange={(e) => setProjectId(e.target.value)} required>
                  <option value="">— Seleccione un Proyecto —</option>
                  {projects.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
                </Select>
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-activity">Actividad Vinculada</Label>
                <Select id="cat-activity" value={activityId} onChange={(e) => setActivityId(e.target.value)} required disabled={!projectId}>
                  <option value="">{projectId ? '— Seleccione Actividad —' : '— Seleccione Proyecto —'}</option>
                  {activities.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
                </Select>
              </div>
            </div>

            <hr className="form-divider" />

            <div className="molecule-field">
              <Label htmlFor="cat-name">Nombre / Descripción de la Tarea Catálogo</Label>
              <textarea
                id="cat-name"
                className="atom-input atom-textarea"
                placeholder="Escriba la acción técnica detallada..."
                value={name}
                onChange={(e) => setName(e.target.value)}
                rows={2}
                required
              />
            </div>

            <div className="form-row form-row--4">
              <div className="molecule-field">
                <Label htmlFor="cat-goal">Meta Cuantitativa</Label>
                <Input id="cat-goal" type="number" step="0.01" min="0" value={goalValue} onChange={(e) => setGoalValue(e.target.value)} />
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-unit">Unidad de Medida</Label>
                <Input id="cat-unit" placeholder="Ej: Informes, Hectáreas" value={goalUnit} onChange={(e) => setGoalUnit(e.target.value)} />
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-weight">Peso / Ponderación (%)</Label>
                <Input id="cat-weight" type="number" step="0.01" min="0" max="100" placeholder="Ej: 25.00" value={weight} onChange={(e) => setWeight(e.target.value)} />
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-type">Clasificación Aporte</Label>
                <Select id="cat-type" value={taskType} onChange={(e) => setTaskType(e.target.value)}>
                  <option value="">Seleccione...</option>
                  <option value="CUMPLIMIENTO_META">Aporta al cumplimiento de la meta</option>
                  <option value="GESTION">Gestión administrativa</option>
                  <option value="APOYO">Apoyo técnico</option>
                </Select>
              </div>
            </div>

            <div className="form-row form-row--3">
              <div className="molecule-field">
                <Label htmlFor="cat-responsible">Responsable (Técnico)</Label>
                <Select id="cat-responsible" value={responsibleUserId} onChange={(e) => setResponsibleUserId(e.target.value)} required>
                  <option value="">— Seleccione Técnico —</option>
                  {tecnicos.map((u) => <option key={u.id} value={u.id}>{u.fullName}</option>)}
                </Select>
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-start">Fecha Inicio</Label>
                <Input id="cat-start" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
              </div>
              <div className="molecule-field">
                <Label htmlFor="cat-due">Fecha Finalización</Label>
                <Input id="cat-due" type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required min={startDate || undefined} />
              </div>
            </div>

            <div className="molecule-field">
              <Label htmlFor="cat-obs">Observaciones (opcional)</Label>
              <textarea
                id="cat-obs"
                className="atom-input atom-textarea"
                placeholder="Detalles adicionales sobre la tarea..."
                value={observation}
                onChange={(e) => setObservation(e.target.value)}
                rows={2}
              />
            </div>

            <Button type="submit" disabled={saving || !activityId} style={{ width: '100%', marginTop: 8 }}>
              {saving ? 'Registrando...' : 'Programar Tarea'}
            </Button>
          </form>
        </div>

        {activityId && (
          <div className="form-card">
            <h2 className="form-card-title">Tareas registradas en esta actividad</h2>
            {tasks.length === 0 ? (
              <p className="empty-state">Esta actividad aún no tiene tareas registradas.</p>
            ) : (
              <div className="task-catalog-list">
                {tasks.map((t, i) => (
                  <div key={t.id} className="task-catalog-item">
                    <span className="task-catalog-number">{i + 1}</span>
                    <div className="task-catalog-info">
                      <span className="task-catalog-name">{t.name}</span>
                      <span className="task-catalog-meta">
                        {t.physicalGoalValue} {t.physicalGoalUnit} · {t.responsibleName}
                        {t.weight != null && ` · ${t.weight}%`}
                      </span>
                    </div>
                    <span className={`task-status-pill task-status-pill--${t.status.toLowerCase()}`}>
                      {t.status.replace('_', ' ')}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
