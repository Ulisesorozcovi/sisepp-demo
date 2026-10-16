import { useEffect, useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { projectApi, activityApi } from '../../api/structureApi';
import { taskApi } from '../../api/taskApi';
import { userApi } from '../../api/userApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { Activity, Project, UserResponse } from '../../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onCreated: () => void;
  preselectedActivityId?: string;
}

export function TaskFormModal({ open, onClose, onCreated, preselectedActivityId }: Props) {
  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState(preselectedActivityId ?? '');
  const [name, setName] = useState('');
  const [goalValue, setGoalValue] = useState('');
  const [goalUnit, setGoalUnit] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [startDate, setStartDate] = useState('');
  const [responsibleUserId, setResponsibleUserId] = useState('');
  const [responsibleName, setResponsibleName] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (open) {
      projectApi.listMine().then(setProjects).catch(() => setProjects([]));
      userApi
        .listAssignable()
        .then(setUsers)
        .catch(() => setUsers([]));
    }
  }, [open]);

  useEffect(() => {
    if (!projectId) {
      setActivities([]);
      return;
    }
    activityApi.list(projectId).then(setActivities).catch(() => setActivities([]));
  }, [projectId]);

  useEffect(() => {
    if (preselectedActivityId) setActivityId(preselectedActivityId);
  }, [preselectedActivityId]);

  if (!open) return null;

  function resetForm() {
    setProjectId('');
    setActivityId('');
    setName('');
    setGoalValue('');
    setGoalUnit('');
    setDueDate('');
    setStartDate('');
    setResponsibleUserId('');
    setResponsibleName('');
    setError(null);
  }

  function handleUserSelect(userId: string) {
    setResponsibleUserId(userId);
    const selected = users.find((u) => u.id === userId);
    if (selected) setResponsibleName(selected.fullName);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!activityId) {
      setError('Selecciona la Actividad a la que pertenece la tarea.');
      return;
    }

    setSaving(true);
    try {
      await taskApi.create({
        activityId,
        name,
        physicalGoalValue: Number(goalValue),
        physicalGoalUnit: goalUnit,
        dueDate,
        responsibleName,
        responsibleUserId,
        startDate: startDate || undefined,
      });
      resetForm();
      onCreated();
      onClose();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo programar la tarea'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ maxWidth: 480 }}>
        <h3>Programar tarea</h3>

        {error && <Alert>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          <div className="molecule-field">
            <Label htmlFor="project">Proyecto</Label>
            <Select id="project" value={projectId} onChange={(e) => { setProjectId(e.target.value); setActivityId(''); }} required>
              <option value="">Selecciona un proyecto</option>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field">
            <Label htmlFor="activity">Actividad</Label>
            <Select id="activity" value={activityId} onChange={(e) => setActivityId(e.target.value)} required disabled={!projectId}>
              <option value="">Selecciona una actividad</option>
              {activities.map((a) => (
                <option key={a.id} value={a.id}>{a.name}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field">
            <Label htmlFor="name">Nombre de la tarea</Label>
            <Input id="name" value={name} onChange={(e) => setName(e.target.value)} placeholder="Reforestación Hectáreas" required />
          </div>

          <div style={{ display: 'flex', gap: 10 }}>
            <div className="molecule-field" style={{ flex: 2 }}>
              <Label htmlFor="goalValue">Meta física</Label>
              <Input id="goalValue" type="number" step="0.01" min="0.01" value={goalValue} onChange={(e) => setGoalValue(e.target.value)} required />
            </div>
            <div className="molecule-field" style={{ flex: 1 }}>
              <Label htmlFor="goalUnit">Unidad</Label>
              <Input id="goalUnit" value={goalUnit} onChange={(e) => setGoalUnit(e.target.value)} placeholder="hectáreas" required />
            </div>
          </div>

          <div style={{ display: 'flex', gap: 10 }}>
            <div className="molecule-field" style={{ flex: 1 }}>
              <Label htmlFor="startDate">Fecha inicio</Label>
              <Input id="startDate" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
            </div>
            <div className="molecule-field" style={{ flex: 1 }}>
              <Label htmlFor="dueDate">Fecha límite</Label>
              <Input id="dueDate" type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
            </div>
          </div>

          <div className="molecule-field">
            <Label htmlFor="responsibleUser">Responsable</Label>
            <Select id="responsibleUser" value={responsibleUserId} onChange={(e) => handleUserSelect(e.target.value)} required>
              <option value="">Selecciona un responsable</option>
              {users.map((u) => (
                <option key={u.id} value={u.id}>{u.fullName} ({u.role})</option>
              ))}
            </Select>
          </div>

          <div className="modal-actions">
            <Button type="button" variant="ghost" onClick={() => { resetForm(); onClose(); }}>Cancelar</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Programar'}</Button>
          </div>
        </form>
      </div>
    </div>
  );
}
