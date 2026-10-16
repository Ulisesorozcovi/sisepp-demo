import { useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { taskApi } from '../../api/taskApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { TaskAlert } from '../../types';

interface Props {
  task: TaskAlert | null;
  /** El backend valida el permiso real; esto solo decide si se muestra el formulario. */
  puedeReprogramar: boolean;
  onClose: () => void;
  onRescheduled: () => void;
}

const ESTADO_LABEL: Record<string, string> = {
  PROGRAMADA: 'Programada',
  EN_EJECUCION: 'En ejecución',
  RETRASADA: 'Retrasada',
  COMPLETADA: 'Completada',
  NO_APLICA: 'No aplica',
  CANCELADA: 'Cancelada',
};

function numero(v: number | null): string {
  if (v === null || v === undefined) return '—';
  return v.toLocaleString('es-CO', { maximumFractionDigits: 2 });
}

export function TaskAlertModal({ task, puedeReprogramar, onClose, onRescheduled }: Props) {
  const [abrirForm, setAbrirForm] = useState(false);
  const [newDueDate, setNewDueDate] = useState('');
  const [justification, setJustification] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  if (!task) return null;

  const vencida = task.dias > 0;
  const enRiesgo = task.senal === 'EN_RIESGO';
  const cumplimiento = task.cumplimiento ?? 0;

  const taskId = task.id;

  async function reprogramar() {
    setError(null);

    if (!newDueDate) {
      setError('Indique la nueva fecha límite.');
      return;
    }
    if (newDueDate <= new Date().toISOString().slice(0, 10)) {
      setError('La nueva fecha debe ser posterior a hoy.');
      return;
    }
    if (justification.trim().length < 10) {
      setError('Debe justificar la reprogramación con al menos 10 caracteres.');
      return;
    }

    setSaving(true);
    try {
      await taskApi.reschedule(taskId, newDueDate, justification.trim());
      onRescheduled();
      onClose();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo reprogramar la tarea.'));
    } finally {
      setSaving(false);
    }
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    void reprogramar();
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card task-modal" onClick={(e) => e.stopPropagation()}>
        <span
          className={`task-modal-flag${
            vencida ? ' task-modal-flag--late' : enRiesgo ? ' task-modal-flag--risk' : ''
          }`}
        >
          {vencida
            ? `Vencida hace ${task.dias} ${task.dias === 1 ? 'día' : 'días'}`
            : enRiesgo
              ? 'En riesgo de incumplimiento'
              : `Vence en ${Math.abs(task.dias)} ${Math.abs(task.dias) === 1 ? 'día' : 'días'}`}
        </span>

        <h3>{task.tarea}</h3>

        <p className="task-modal-path">
          {task.proyectoCodigo ? `${task.proyectoCodigo} · ` : ''}
          {task.proyecto}
          {task.actividad ? ` › ${task.actividad}` : ''}
        </p>

        <dl className="task-modal-grid">
          <div>
            <dt>Responsable</dt>
            <dd>{task.responsable}</dd>
          </div>
          <div>
            <dt>Estado</dt>
            <dd>{ESTADO_LABEL[task.estado] ?? task.estado}</dd>
          </div>
          <div>
            <dt>Inicio</dt>
            <dd>{task.fechaInicio ?? '—'}</dd>
          </div>
          <div>
            <dt>Vencimiento</dt>
            <dd className={vencida ? 'task-modal-late' : undefined}>{task.fechaVencimiento}</dd>
          </div>
          <div>
            <dt>Meta física</dt>
            <dd>
              {numero(task.meta)} {task.unidad ?? ''}
            </dd>
          </div>
          <div>
            <dt>Reportado</dt>
            <dd>
              {numero(task.reportado)} {task.unidad ?? ''}
            </dd>
          </div>
        </dl>

        <div className="task-modal-progress">
          <div className="task-modal-progress-head">
            <span>Cumplimiento de la meta</span>
            <strong>{cumplimiento}%</strong>
          </div>
          <div className="dash-bar-track">
            <div
              className="dash-bar-fill"
              style={{
                width: `${Math.min(cumplimiento, 100)}%`,
                background:
                  cumplimiento >= 70
                    ? 'var(--green-600)'
                    : cumplimiento >= 40
                      ? 'var(--yellow-600)'
                      : cumplimiento > 0
                        ? 'var(--orange-600)'
                        : 'var(--ink-400)',
              }}
            />
          </div>
        </div>

        {task.tiempoTranscurrido !== null && task.ritmo !== null && (
          <div className="task-modal-pace">
            <div className="task-modal-pace-row">
              <span>Plazo consumido</span>
              <strong>{Math.round(task.tiempoTranscurrido * 100)}%</strong>
            </div>
            <div className="task-modal-pace-row">
              <span>Ritmo frente a lo esperado</span>
              <strong className={task.ritmo < 0.7 ? 'task-modal-late' : undefined}>
                {Math.round(task.ritmo * 100)}%
              </strong>
            </div>
            <p className="task-modal-hint">
              {task.ritmo < 0.7
                ? 'El avance va muy por debajo del esperado para el tiempo transcurrido.'
                : task.ritmo < 0.9
                  ? 'El avance va algo por debajo de lo esperado.'
                  : 'El avance va acorde con el plazo.'}
            </p>
          </div>
        )}

        {task.notaRiesgo && (
          <div className="task-modal-note task-modal-note--risk">
            <span className="task-modal-note-label">Causa registrada por el coordinador</span>
            <p>{task.notaRiesgo}</p>
          </div>
        )}

        {task.observacion && (
          <div className="task-modal-note">
            <span className="task-modal-note-label">Observación</span>
            <p>{task.observacion}</p>
          </div>
        )}

        {error && <Alert>{error}</Alert>}

        {puedeReprogramar && abrirForm && (
          <form onSubmit={handleSubmit} className="task-modal-form">
            <div className="molecule-field">
              <Label htmlFor="alertNewDueDate">Nueva fecha límite</Label>
              <Input
                id="alertNewDueDate"
                type="date"
                value={newDueDate}
                min={new Date(Date.now() + 86400000).toISOString().slice(0, 10)}
                onChange={(e) => setNewDueDate(e.target.value)}
                required
              />
            </div>

            <div className="molecule-field">
              <Label htmlFor="alertJustification">Justificación</Label>
              <textarea
                id="alertJustification"
                className="atom-input"
                rows={3}
                value={justification}
                onChange={(e) => setJustification(e.target.value)}
                placeholder="Ej. Las jornadas se aplazaron por temporada de lluvias en la zona."
                required
              />
              <span className="task-modal-hint">
                Queda registrada en el historial junto con su usuario y la fecha anterior.
              </span>
            </div>
          </form>
        )}

        <div className="modal-actions">
          <Button type="button" variant="ghost" onClick={onClose}>
            Cerrar
          </Button>

          {puedeReprogramar && !abrirForm && (
            <Button type="button" onClick={() => setAbrirForm(true)}>
              Reprogramar
            </Button>
          )}

          {puedeReprogramar && abrirForm && (
            <Button type="button" disabled={saving} onClick={() => void reprogramar()}>
              {saving ? 'Guardando…' : 'Confirmar reprogramación'}
            </Button>
          )}
        </div>

        {!puedeReprogramar && (
          <p className="task-modal-hint task-modal-hint--center">
            Su rol no permite reprogramar tareas. Solicítelo al coordinador del proyecto.
          </p>
        )}
      </div>
    </div>
  );
}
