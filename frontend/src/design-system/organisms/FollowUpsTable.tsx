import { useEffect, useState } from 'react';
import { followUpApi } from '../../api/followUpApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { useAuth } from '../../context/AuthContext';
import type { FollowUp, FollowUpStatus } from '../../types';

const STATUS_LABELS: Record<FollowUpStatus, string> = {
  EN_PROCESO: 'En proceso',
  CUMPLIDA: 'Cumplida',
  REZAGADA: 'Rezagada',
  NO_APLICA: 'No aplica',
};

const now = new Date();

function formatDateTime(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
    + ' ' + d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
}

interface Props {
  refreshKey: number;
  /** El backend revalida el permiso; esto solo decide si se muestra el botón. */
  puedeEditar?: boolean;
  onEdit: (followUp: FollowUp) => void;
}

export function FollowUpsTable({ refreshKey, puedeEditar = true, onEdit }: Props) {
  const { hasRole } = useAuth();
  const [followUps, setFollowUps] = useState<FollowUp[]>([]);
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState<number | ''>(now.getMonth() + 1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    followUpApi
      .list({
        taskId: undefined,
        year,
        month: month || undefined,
      })
      .then((data) => {
        if (active) setFollowUps(data);
      })
      .catch((err) => {
        if (active) setError(getApiErrorMessage(err, 'No se pudieron cargar los seguimientos'));
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [year, month, refreshKey]);

  const years = Array.from({ length: 5 }, (_, i) => now.getFullYear() - 2 + i);
  const canDelete = hasRole('DIRECTOR') || hasRole('STAFF');

  async function handleDelete(id: string) {
    try {
      await followUpApi.remove(id);
      setFollowUps((prev) => prev.filter((f) => f.id !== id));
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo eliminar'));
    }
  }

  return (
    <div className="organism-table-card">
      <div className="task-filters">
        <Select value={month} onChange={(e) => setMonth(e.target.value ? Number(e.target.value) : '')} style={{ maxWidth: 160 }}>
          <option value="">Todos los meses</option>
          {Array.from({ length: 12 }, (_, i) => (
            <option key={i + 1} value={i + 1}>
              {new Date(2000, i).toLocaleString('es', { month: 'long' })}
            </option>
          ))}
        </Select>
        <Select value={year} onChange={(e) => setYear(Number(e.target.value))} style={{ maxWidth: 120 }}>
          {years.map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </Select>
      </div>

      {loading && <div className="empty-state">Cargando seguimientos...</div>}
      {error && <div className="empty-state">{error}</div>}

      {!loading && !error && (
        <table className="users-table">
          <thead>
            <tr>
              <th>Tarea</th>
              <th>Período</th>
              <th>Avance</th>
              <th>Estado</th>
              <th>Evidencia</th>
              <th>Adjunto</th>
              <th>Reportado por</th>
              <th>Fecha reporte</th>
              <th>Observaciones</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {followUps.length === 0 && (
              <tr>
                <td colSpan={10} className="empty-state">No hay seguimientos para este período.</td>
              </tr>
            )}
            {followUps.map((fu) => (
              <tr key={fu.id}>
                <td style={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
                    title={fu.taskName ?? undefined}>
                  {fu.taskName ?? '—'}
                </td>
                <td style={{ whiteSpace: 'nowrap' }}>
                  {new Date(2000, fu.reportedMonth - 1).toLocaleString('es', { month: 'short' })} {fu.reportedYear}
                </td>
                <td>{fu.physicalProgress ?? '—'}</td>
                <td>
                  <span className={`followup-status-pill followup-status-pill--${fu.status.toLowerCase()}`}>
                    {STATUS_LABELS[fu.status]}
                  </span>
                </td>
                <td>
                  {fu.evidenceUrl ? (
                    <a href={fu.evidenceUrl} target="_blank" rel="noopener noreferrer" style={{ color: 'var(--blue-600)', fontSize: 12, fontWeight: 600 }}>
                      Ver
                    </a>
                  ) : '—'}
                </td>
                <td>
                  {fu.attachmentUrl ? (
                    <a href={fu.attachmentUrl} target="_blank" rel="noopener noreferrer" style={{ color: 'var(--blue-600)', fontSize: 12, fontWeight: 600 }}>
                      Descargar
                    </a>
                  ) : '—'}
                </td>
                <td>{fu.reportedByName ?? '—'}</td>
                <td style={{ whiteSpace: 'nowrap', fontSize: 12 }}>
                  {fu.createdAt ? formatDateTime(fu.createdAt) : '—'}
                  {fu.updatedAt && fu.updatedAt !== fu.createdAt && (
                    <div style={{ color: 'var(--ink-400)', fontSize: 11 }}>
                      Editado: {formatDateTime(fu.updatedAt)}
                    </div>
                  )}
                </td>
                <td style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {fu.observations ?? '—'}
                </td>
                <td>
                  <div className="row-actions">
                    {puedeEditar && <button onClick={() => onEdit(fu)}>Editar</button>}
                    {canDelete && (
                      <button className="danger" onClick={() => handleDelete(fu.id)}>Eliminar</button>
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
