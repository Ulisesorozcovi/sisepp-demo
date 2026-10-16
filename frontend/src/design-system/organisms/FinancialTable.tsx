import { useEffect, useState } from 'react';
import { financialApi } from '../../api/financialApi';
import { projectApi } from '../../api/structureApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { useAuth } from '../../context/AuthContext';
import type { FinancialExecution, Project } from '../../types';

const COP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

const now = new Date();

interface Props {
  refreshKey: number;
  onAdd: () => void;
  onEdit: (record: FinancialExecution) => void;
}

export function FinancialTable({ refreshKey, onAdd, onEdit }: Props) {
  const { hasRole } = useAuth();
  const [projects, setProjects] = useState<Project[]>([]);
  const [records, setRecords] = useState<FinancialExecution[]>([]);
  const [projectId, setProjectId] = useState('');
  const [year, setYear] = useState(now.getFullYear());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    projectApi.list({}).then(setProjects).catch(() => setProjects([]));
  }, []);

  useEffect(() => {
    let active = true;
    setLoading(true);
    financialApi
      .list({ projectId: projectId || undefined, year })
      .then((data) => { if (active) setRecords(data); })
      .catch((err) => { if (active) setError(getApiErrorMessage(err, 'No se pudieron cargar los registros')); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [projectId, year, refreshKey]);

  const canWrite = hasRole('DIRECTOR') || hasRole('STAFF') || hasRole('COORDINADOR');
  const canDelete = hasRole('DIRECTOR');
  const years = Array.from({ length: 5 }, (_, i) => now.getFullYear() - 2 + i);

  async function handleDelete(id: string) {
    try {
      await financialApi.remove(id);
      setRecords((prev) => prev.filter((r) => r.id !== id));
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo eliminar'));
    }
  }

  return (
    <div className="organism-table-card">
      <div className="task-filters">
        <Select value={projectId} onChange={(e) => setProjectId(e.target.value)} style={{ maxWidth: 260 }}>
          <option value="">Todos los proyectos</option>
          {projects.map((p) => (
            <option key={p.id} value={p.id}>{p.name}</option>
          ))}
        </Select>
        <Select value={year} onChange={(e) => setYear(Number(e.target.value))} style={{ maxWidth: 120 }}>
          {years.map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </Select>
        <div style={{ flex: 1 }} />
        {canWrite && <Button onClick={onAdd}>+ Nuevo registro</Button>}
      </div>

      {loading && <div className="empty-state">Cargando registros...</div>}
      {error && <div className="empty-state">{error}</div>}

      {!loading && !error && (
        <table className="users-table">
          <thead>
            <tr>
              <th>Período</th>
              <th>Presupuesto</th>
              <th>Compromisos</th>
              <th>Obligaciones</th>
              <th>Pagos</th>
              <th>Saldo</th>
              <th>% Ejec.</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {records.length === 0 && (
              <tr>
                <td colSpan={8} className="empty-state">No hay registros financieros para este período.</td>
              </tr>
            )}
            {records.map((r) => {
              const pct = Math.min(r.executionPercentage ?? 0, 100);
              return (
                <tr key={r.id}>
                  <td>
                    {new Date(2000, r.periodMonth - 1).toLocaleString('es', { month: 'short' })} {r.periodYear}
                  </td>
                  <td>{COP.format(r.approvedBudget)}</td>
                  <td>{COP.format(r.commitments)}</td>
                  <td>{COP.format(r.obligations)}</td>
                  <td>{COP.format(r.payments)}</td>
                  <td>{COP.format(r.availableBalance)}</td>
                  <td>
                    <div className="fin-pct-cell">
                      <div className="fin-pct-bar">
                        <div
                          className="fin-pct-fill"
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                      <span className="fin-pct-label">{pct.toFixed(1)}%</span>
                    </div>
                  </td>
                  <td>
                    <div className="row-actions">
                      {canWrite && <button onClick={() => onEdit(r)}>Editar</button>}
                      {canDelete && <button className="danger" onClick={() => handleDelete(r.id)}>Eliminar</button>}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}
