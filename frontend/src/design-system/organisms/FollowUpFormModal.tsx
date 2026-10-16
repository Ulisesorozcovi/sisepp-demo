import { useEffect, useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { followUpApi } from '../../api/followUpApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { Task, FollowUp, FollowUpStatus } from '../../types';

interface Props {
  /** Tarea sobre la que se registra un avance nuevo. */
  task?: Task | null;
  /** Seguimiento existente. Si viene, el modal entra en modo edición. */
  followUp?: FollowUp | null;
  onClose: () => void;
  /** Se dispara tras crear o actualizar. */
  onCreated: () => void;
}

const STATUS_OPTIONS: { value: FollowUpStatus; label: string }[] = [
  { value: 'EN_PROCESO', label: 'En proceso' },
  { value: 'CUMPLIDA', label: 'Cumplida' },
  { value: 'REZAGADA', label: 'Rezagada' },
  { value: 'NO_APLICA', label: 'No aplica' },
];

const now = new Date();

export function FollowUpFormModal({ task, followUp, onClose, onCreated }: Props) {
  const editando = !!followUp;

  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [progress, setProgress] = useState('');
  const [evidenceUrl, setEvidenceUrl] = useState('');
  const [attachmentUrl, setAttachmentUrl] = useState('');
  const [status, setStatus] = useState<FollowUpStatus>('EN_PROCESO');
  const [observations, setObservations] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  // Al abrir en modo edición se precargan los valores del reporte existente.
  useEffect(() => {
    if (followUp) {
      setMonth(followUp.reportedMonth);
      setYear(followUp.reportedYear);
      setProgress(followUp.physicalProgress != null ? String(followUp.physicalProgress) : '');
      setEvidenceUrl(followUp.evidenceUrl ?? '');
      setAttachmentUrl(followUp.attachmentUrl ?? '');
      setStatus(followUp.status);
      setObservations(followUp.observations ?? '');
      setError(null);
    }
  }, [followUp]);

  if (!task && !followUp) return null;

  function resetForm() {
    setProgress('');
    setEvidenceUrl('');
    setAttachmentUrl('');
    setStatus('EN_PROCESO');
    setObservations('');
    setError(null);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSaving(true);
    try {
      if (editando) {
        // El periodo no se toca: es parte de la clave única del reporte.
        // Las cadenas vacías se envían tal cual para poder borrar un valor.
        await followUpApi.update(followUp!.id, {
          physicalProgress: progress === '' ? undefined : Number(progress),
          evidenceUrl: evidenceUrl.trim(),
          attachmentUrl: attachmentUrl.trim(),
          status,
          observations: observations.trim(),
        });
      } else {
        await followUpApi.create({
          taskId: task!.id,
          reportedMonth: month,
          reportedYear: year,
          physicalProgress: Number(progress) || undefined,
          evidenceUrl: evidenceUrl.trim() || undefined,
          attachmentUrl: attachmentUrl.trim() || undefined,
          status,
          observations: observations.trim() || undefined,
        });
      }
      resetForm();
      onCreated();
      onClose();
    } catch (err) {
      const anyErr = err as { response?: { status?: number } };
      if (anyErr?.response?.status === 409) {
        setError('Ya existe un reporte para esta tarea en ese mes. Edítelo desde el historial.');
      } else if (anyErr?.response?.status === 403) {
        setError('Su rol no permite modificar reportes de avance.');
      } else {
        setError(getApiErrorMessage(
          err,
          editando ? 'No se pudo actualizar el reporte' : 'No se pudo registrar el seguimiento',
        ));
      }
    } finally {
      setSaving(false);
    }
  }

  const years = Array.from({ length: 5 }, (_, i) => now.getFullYear() - 2 + i);

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <h3>{editando ? 'Editar avance reportado' : 'Registrar avance'}</h3>
        <p style={{ fontSize: 13, color: 'var(--ink-600)', marginTop: -10, marginBottom: 16 }}>
          {editando ? (followUp!.taskName ?? 'Tarea sin nombre') : task!.name}
          {editando && (
            <>
              {' · '}
              {new Date(2000, followUp!.reportedMonth - 1).toLocaleString('es', { month: 'long' })}{' '}
              {followUp!.reportedYear}
            </>
          )}
        </p>

        {error && <Alert>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          {!editando && (
          <div style={{ display: 'flex', gap: 10 }}>
            <div className="molecule-field" style={{ flex: 1 }}>
              <Label htmlFor="fu-month">Mes</Label>
              <Select id="fu-month" value={month} onChange={(e) => setMonth(Number(e.target.value))}>
                {Array.from({ length: 12 }, (_, i) => (
                  <option key={i + 1} value={i + 1}>
                    {new Date(2000, i).toLocaleString('es', { month: 'long' })}
                  </option>
                ))}
              </Select>
            </div>
            <div className="molecule-field" style={{ flex: 1 }}>
              <Label htmlFor="fu-year">Año</Label>
              <Select id="fu-year" value={year} onChange={(e) => setYear(Number(e.target.value))}>
                {years.map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </Select>
            </div>
          </div>
          )}

          <div className="molecule-field">
            <Label htmlFor="fu-progress">Avance físico</Label>
            <Input
              id="fu-progress"
              type="number"
              step="0.01"
              min="0"
              value={progress}
              onChange={(e) => setProgress(e.target.value)}
              placeholder="Ej: 25.5"
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="fu-evidence">URL de evidencia</Label>
            <Input
              id="fu-evidence"
              type="url"
              value={evidenceUrl}
              onChange={(e) => setEvidenceUrl(e.target.value)}
              placeholder="https://..."
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="fu-attachment">URL del adjunto</Label>
            <Input
              id="fu-attachment"
              type="url"
              value={attachmentUrl}
              onChange={(e) => setAttachmentUrl(e.target.value)}
              placeholder="https://..."
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="fu-status">Estado</Label>
            <Select id="fu-status" value={status} onChange={(e) => setStatus(e.target.value as FollowUpStatus)}>
              {STATUS_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field" style={{ marginBottom: 6 }}>
            <Label htmlFor="fu-obs">Observaciones</Label>
            <textarea
              id="fu-obs"
              className="atom-input"
              rows={3}
              value={observations}
              onChange={(e) => setObservations(e.target.value)}
              placeholder="Notas adicionales sobre el avance"
            />
          </div>

          <div className="modal-actions">
            <Button type="button" variant="ghost" onClick={() => { resetForm(); onClose(); }}>Cancelar</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Guardando…' : editando ? 'Guardar cambios' : 'Guardar'}</Button>
          </div>
        </form>
      </div>
    </div>
  );
}
