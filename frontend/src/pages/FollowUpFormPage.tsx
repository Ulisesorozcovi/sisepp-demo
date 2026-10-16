import { useEffect, useState, type FormEvent } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { Label } from '../design-system/atoms/Label';
import { Input } from '../design-system/atoms/Input';
import { Select } from '../design-system/atoms/Select';
import { Button } from '../design-system/atoms/Button';
import { Alert } from '../design-system/molecules/Alert';
import { useAuth } from '../context/AuthContext';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { followUpApi } from '../api/followUpApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { Project, Activity, Task, FollowUpStatus, FollowUp } from '../types';

const STATUS_OPTIONS: { value: FollowUpStatus; label: string }[] = [
  { value: 'EN_PROCESO', label: 'En proceso' },
  { value: 'CUMPLIDA', label: 'Cumplida' },
  { value: 'REZAGADA', label: 'Rezagada' },
  { value: 'NO_APLICA', label: 'No aplica' },
];

const now = new Date();

export function FollowUpFormPage() {
  const { user, hasRole } = useAuth();
  const isTecnico = hasRole('TECNICO');

  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);

  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState('');
  const [taskId, setTaskId] = useState('');
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [progress, setProgress] = useState('');
  const [status, setStatus] = useState<FollowUpStatus>('EN_PROCESO');
  const [evidenceUrl, setEvidenceUrl] = useState('');
  const [attachmentUrl, setAttachmentUrl] = useState('');
  const [observations, setObservations] = useState('');

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [duplicateFollowUp, setDuplicateFollowUp] = useState<FollowUp | null>(null);
  const [showDuplicateDialog, setShowDuplicateDialog] = useState(false);

  const [loadingProjects, setLoadingProjects] = useState(true);
  const [loadingActivities, setLoadingActivities] = useState(false);
  const [loadingTasks, setLoadingTasks] = useState(false);

  const years = Array.from({ length: 5 }, (_, i) => now.getFullYear() - 2 + i);

  useEffect(() => {
    if (isTecnico) {
      setLoadingTasks(true);
      taskApi.list({}).then((data) => {
        setTasks(data);
        setLoadingTasks(false);
      }).catch(() => setLoadingTasks(false));
      setLoadingProjects(false);
    } else {
      projectApi.list({}).then((data) => {
        setProjects(data);
        setLoadingProjects(false);
      }).catch(() => setLoadingProjects(false));
    }
  }, [isTecnico]);

  useEffect(() => {
    if (!projectId || isTecnico) {
      setActivities([]);
      setActivityId('');
      return;
    }
    setLoadingActivities(true);
    setActivityId('');
    setTasks([]);
    setTaskId('');
    activityApi.list(projectId).then((data) => {
      setActivities(data);
      setLoadingActivities(false);
    }).catch(() => setLoadingActivities(false));
  }, [projectId, isTecnico]);

  useEffect(() => {
    if (!activityId || isTecnico) {
      if (!isTecnico) {
        setTasks([]);
        setTaskId('');
      }
      return;
    }
    setLoadingTasks(true);
    setTaskId('');
    taskApi.list({ activityId }).then((data) => {
      setTasks(data);
      setLoadingTasks(false);
    }).catch(() => setLoadingTasks(false));
  }, [activityId, isTecnico]);

  function handleTaskChange(newTaskId: string) {
    setTaskId(newTaskId);
    if (isTecnico && newTaskId) {
      const task = tasks.find((t) => t.id === newTaskId);
      if (task) {
        setActivityId(task.activityId);
      }
    }
  }

  function resetForm() {
    if (!isTecnico) {
      setProjectId('');
      setActivityId('');
      setTaskId('');
    } else {
      setTaskId('');
    }
    setMonth(now.getMonth() + 1);
    setYear(now.getFullYear());
    setProgress('');
    setStatus('EN_PROCESO');
    setEvidenceUrl('');
    setAttachmentUrl('');
    setObservations('');
    setError(null);
    setDuplicateFollowUp(null);
    setShowDuplicateDialog(false);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!taskId) {
      setError('Debe seleccionar una tarea.');
      return;
    }
    setError(null);
    setSuccess(null);
    setSaving(true);

    try {
      await followUpApi.create({
        taskId,
        reportedMonth: month,
        reportedYear: year,
        physicalProgress: progress ? Number(progress) : undefined,
        evidenceUrl: evidenceUrl.trim() || undefined,
        attachmentUrl: attachmentUrl.trim() || undefined,
        status,
        observations: observations.trim() || undefined,
      });
      setSuccess('Avance registrado correctamente.');
      resetForm();
    } catch (err) {
      const axiosErr = err as { response?: { status?: number } };
      if (axiosErr?.response?.status === 409) {
        try {
          const existing = await followUpApi.list({ taskId, year, month });
          if (existing.length > 0) {
            setDuplicateFollowUp(existing[0]);
            setShowDuplicateDialog(true);
          }
        } catch {
          // ignore lookup error
        }
        setError('Ya existe un registro de avance para esta tarea en el período seleccionado.');
      } else {
        setError(getApiErrorMessage(err, 'No se pudo registrar el avance.'));
      }
    } finally {
      setSaving(false);
    }
  }

  async function handleEditExisting() {
    if (!duplicateFollowUp) return;
    setError(null);
    setSaving(true);
    try {
      await followUpApi.update(duplicateFollowUp.id, {
        physicalProgress: progress ? Number(progress) : undefined,
        evidenceUrl: evidenceUrl.trim() || undefined,
        attachmentUrl: attachmentUrl.trim() || undefined,
        status,
        observations: observations.trim() || undefined,
      });
      setSuccess('Registro existente actualizado correctamente.');
      setShowDuplicateDialog(false);
      resetForm();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo actualizar el registro existente.'));
    } finally {
      setSaving(false);
    }
  }

  const selectedTask = tasks.find((t) => t.id === taskId);

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Registrar Avance Físico</h1>
          <p>Seleccione la tarea y registre el avance del período correspondiente.</p>
        </div>
      </div>

      <div className="form-card">
        {error && <Alert>{error}</Alert>}
        {success && <div className="alert-success">{success}</div>}

        {showDuplicateDialog && duplicateFollowUp && (
          <div className="duplicate-dialog">
            <p>
              Ya existe un avance para <strong>{selectedTask?.name ?? 'esta tarea'}</strong> en{' '}
              {new Date(2000, month - 1).toLocaleString('es', { month: 'long' })} {year}.
            </p>
            <p style={{ fontSize: 13, color: 'var(--ink-600)' }}>
              Avance actual: {duplicateFollowUp.physicalProgress ?? 0} — Estado:{' '}
              {STATUS_OPTIONS.find((o) => o.value === duplicateFollowUp.status)?.label ?? duplicateFollowUp.status}
            </p>
            <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
              <Button onClick={handleEditExisting} disabled={saving}>
                {saving ? 'Actualizando...' : 'Actualizar registro existente'}
              </Button>
              <Button variant="ghost" onClick={() => { setShowDuplicateDialog(false); setDuplicateFollowUp(null); setError(null); }}>
                Cancelar
              </Button>
            </div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {!isTecnico && (
            <>
              <div className="molecule-field">
                <Label htmlFor="fuf-project">Proyecto</Label>
                <Select
                  id="fuf-project"
                  value={projectId}
                  onChange={(e) => setProjectId(e.target.value)}
                  disabled={loadingProjects}
                >
                  <option value="">
                    {loadingProjects ? 'Cargando proyectos...' : '— Seleccione un proyecto —'}
                  </option>
                  {projects.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.code ? `${p.code} — ` : ''}{p.name}
                    </option>
                  ))}
                </Select>
              </div>

              <div className="molecule-field">
                <Label htmlFor="fuf-activity">Actividad</Label>
                <Select
                  id="fuf-activity"
                  value={activityId}
                  onChange={(e) => setActivityId(e.target.value)}
                  disabled={!projectId || loadingActivities}
                >
                  <option value="">
                    {!projectId
                      ? '— Seleccione un proyecto primero —'
                      : loadingActivities
                        ? 'Cargando actividades...'
                        : '— Seleccione una actividad —'}
                  </option>
                  {activities.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.code ? `${a.code} — ` : ''}{a.name}
                    </option>
                  ))}
                </Select>
              </div>

              <div className="molecule-field">
                <Label htmlFor="fuf-task">Tarea</Label>
                <Select
                  id="fuf-task"
                  value={taskId}
                  onChange={(e) => setTaskId(e.target.value)}
                  disabled={!activityId || loadingTasks}
                >
                  <option value="">
                    {!activityId
                      ? '— Seleccione una actividad primero —'
                      : loadingTasks
                        ? 'Cargando tareas...'
                        : '— Seleccione una tarea —'}
                  </option>
                  {tasks.map((t) => (
                    <option key={t.id} value={t.id}>{t.name}</option>
                  ))}
                </Select>
              </div>
            </>
          )}

          {isTecnico && (
            <div className="molecule-field">
              <Label htmlFor="fuf-task">Tarea asignada</Label>
              <Select
                id="fuf-task"
                value={taskId}
                onChange={(e) => handleTaskChange(e.target.value)}
                disabled={loadingTasks}
              >
                <option value="">
                  {loadingTasks ? 'Cargando tareas...' : '— Seleccione una tarea —'}
                </option>
                {tasks.map((t) => (
                  <option key={t.id} value={t.id}>{t.name}</option>
                ))}
              </Select>
            </div>
          )}

          {selectedTask && (
            <div className="followup-task-summary">
              <span><strong>Meta:</strong> {selectedTask.physicalGoalValue} {selectedTask.physicalGoalUnit}</span>
              <span><strong>Responsable:</strong> {selectedTask.responsibleName}</span>
              <span><strong>Vencimiento:</strong> {selectedTask.dueDate}</span>
            </div>
          )}

          <div className="form-card__row">
            <div className="molecule-field">
              <Label htmlFor="fuf-month">Mes</Label>
              <Select id="fuf-month" value={month} onChange={(e) => setMonth(Number(e.target.value))}>
                {Array.from({ length: 12 }, (_, i) => (
                  <option key={i + 1} value={i + 1}>
                    {new Date(2000, i).toLocaleString('es', { month: 'long' })}
                  </option>
                ))}
              </Select>
            </div>
            <div className="molecule-field">
              <Label htmlFor="fuf-year">Año</Label>
              <Select id="fuf-year" value={year} onChange={(e) => setYear(Number(e.target.value))}>
                {years.map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </Select>
            </div>
          </div>

          <div className="molecule-field">
            <Label htmlFor="fuf-progress">Avance físico</Label>
            <Input
              id="fuf-progress"
              type="number"
              step="0.01"
              min="0"
              value={progress}
              onChange={(e) => setProgress(e.target.value)}
              placeholder="Ej: 25.5"
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="fuf-status">Estado</Label>
            <Select id="fuf-status" value={status} onChange={(e) => setStatus(e.target.value as FollowUpStatus)}>
              {STATUS_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field">
            <Label htmlFor="fuf-evidence">URL de evidencia</Label>
            <Input
              id="fuf-evidence"
              type="url"
              value={evidenceUrl}
              onChange={(e) => setEvidenceUrl(e.target.value)}
              placeholder="https://..."
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="fuf-attachment">Archivo adjunto</Label>
            <span style={{ fontSize: 12, color: 'var(--ink-500)', marginTop: -4, display: 'block' }}>No obligatorio</span>
            <Input
              id="fuf-attachment"
              type="url"
              value={attachmentUrl}
              onChange={(e) => setAttachmentUrl(e.target.value)}
              placeholder="https://..."
            />
          </div>

          <div className="molecule-field" style={{ marginBottom: 6 }}>
            <Label htmlFor="fuf-obs">Observaciones</Label>
            <textarea
              id="fuf-obs"
              className="atom-input"
              rows={3}
              value={observations}
              onChange={(e) => setObservations(e.target.value)}
              placeholder="Notas adicionales sobre el avance"
            />
          </div>

          <div className="form-card__actions">
            <Button type="button" variant="ghost" onClick={resetForm}>
              Limpiar
            </Button>
            <Button type="submit" disabled={saving || !taskId}>
              {saving ? 'Guardando...' : 'Registrar avance'}
            </Button>
          </div>
        </form>
      </div>
    </DashboardLayout>
  );
}
