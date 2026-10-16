import { useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { taskApi } from '../../api/taskApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { Task } from '../../types';

interface Props {
  task: Task | null;
  onClose: () => void;
  onRescheduled: () => void;
}

export function RescheduleModal({ task, onClose, onRescheduled }: Props) {
  const [newDueDate, setNewDueDate] = useState('');
  const [justification, setJustification] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  if (!task) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (justification.trim().length < 10) {
      setError('Debe justificar la reprogramación (mínimo 10 caracteres).');
      return;
    }

    setSaving(true);
    try {
      await taskApi.reschedule(task!.id, newDueDate, justification.trim());
      setNewDueDate('');
      setJustification('');
      onRescheduled();
      onClose();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo reprogramar la tarea'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <h3>Reprogramar tarea</h3>
        <p style={{ fontSize: 13, color: 'var(--ink-600)', marginTop: -10, marginBottom: 16 }}>
          {task.name} · fecha actual: {task.dueDate}
        </p>

        {error && <Alert>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          <div className="molecule-field">
            <Label htmlFor="newDueDate">Nueva fecha límite</Label>
            <Input id="newDueDate" type="date" value={newDueDate} onChange={(e) => setNewDueDate(e.target.value)} required />
          </div>

          <div className="molecule-field" style={{ marginBottom: 6 }}>
            <Label htmlFor="justification">Justificación</Label>
            <textarea
              id="justification"
              className="atom-input"
              rows={3}
              value={justification}
              onChange={(e) => setJustification(e.target.value)}
              placeholder="Ej. Retraso por temporada de lluvias"
              required
            />
          </div>

          <div className="modal-actions">
            <Button type="button" variant="ghost" onClick={onClose}>Cancelar</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Confirmar'}</Button>
          </div>
        </form>
      </div>
    </div>
  );
}
