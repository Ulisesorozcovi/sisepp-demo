import { useEffect, useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { financialApi } from '../../api/financialApi';
import { projectApi } from '../../api/structureApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { FinancialExecution, Project } from '../../types';

interface Props {
  open: boolean;
  editing: FinancialExecution | null;
  onClose: () => void;
  onSaved: () => void;
}

const now = new Date();

export function FinancialFormModal({ open, editing, onClose, onSaved }: Props) {
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectId, setProjectId] = useState('');
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [approvedBudget, setApprovedBudget] = useState('');
  const [commitments, setCommitments] = useState('');
  const [obligations, setObligations] = useState('');
  const [payments, setPayments] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (open) {
      projectApi.list({}).then(setProjects).catch(() => setProjects([]));
    }
  }, [open]);

  useEffect(() => {
    if (editing) {
      setProjectId(editing.projectId);
      setMonth(editing.periodMonth);
      setYear(editing.periodYear);
      setApprovedBudget(String(editing.approvedBudget));
      setCommitments(String(editing.commitments));
      setObligations(String(editing.obligations));
      setPayments(String(editing.payments));
    }
  }, [editing]);

  if (!open) return null;

  function resetForm() {
    setProjectId('');
    setApprovedBudget('');
    setCommitments('');
    setObligations('');
    setPayments('');
    setError(null);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSaving(true);
    try {
      if (editing) {
        await financialApi.update(editing.id, {
          approvedBudget: Number(approvedBudget) || undefined,
          commitments: Number(commitments) || undefined,
          obligations: Number(obligations) || undefined,
          payments: Number(payments) || undefined,
        });
      } else {
        await financialApi.create({
          projectId,
          periodMonth: month,
          periodYear: year,
          approvedBudget: Number(approvedBudget),
          commitments: Number(commitments) || undefined,
          obligations: Number(obligations) || undefined,
          payments: Number(payments) || undefined,
        });
      }
      resetForm();
      onSaved();
      onClose();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo guardar el registro financiero'));
    } finally {
      setSaving(false);
    }
  }

  const years = Array.from({ length: 5 }, (_, i) => now.getFullYear() - 2 + i);

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ maxWidth: 480 }}>
        <h3>{editing ? 'Editar ejecución' : 'Nueva ejecución financiera'}</h3>

        {error && <Alert>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          {!editing && (
            <>
              <div className="molecule-field">
                <Label htmlFor="fin-project">Proyecto</Label>
                <Select id="fin-project" value={projectId} onChange={(e) => setProjectId(e.target.value)} required>
                  <option value="">Selecciona un proyecto</option>
                  {projects.map((p) => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </Select>
              </div>

              <div style={{ display: 'flex', gap: 10 }}>
                <div className="molecule-field" style={{ flex: 1 }}>
                  <Label htmlFor="fin-month">Mes</Label>
                  <Select id="fin-month" value={month} onChange={(e) => setMonth(Number(e.target.value))}>
                    {Array.from({ length: 12 }, (_, i) => (
                      <option key={i + 1} value={i + 1}>
                        {new Date(2000, i).toLocaleString('es', { month: 'long' })}
                      </option>
                    ))}
                  </Select>
                </div>
                <div className="molecule-field" style={{ flex: 1 }}>
                  <Label htmlFor="fin-year">Año</Label>
                  <Select id="fin-year" value={year} onChange={(e) => setYear(Number(e.target.value))}>
                    {years.map((y) => (
                      <option key={y} value={y}>{y}</option>
                    ))}
                  </Select>
                </div>
              </div>
            </>
          )}

          <div className="molecule-field">
            <Label htmlFor="fin-budget">Presupuesto definitivo</Label>
            <Input id="fin-budget" type="number" step="1" min="0" value={approvedBudget} onChange={(e) => setApprovedBudget(e.target.value)} required />
          </div>
          <div className="molecule-field">
            <Label htmlFor="fin-commit">Compromisos</Label>
            <Input id="fin-commit" type="number" step="1" min="0" value={commitments} onChange={(e) => setCommitments(e.target.value)} />
          </div>
          <div className="molecule-field">
            <Label htmlFor="fin-oblig">Obligaciones</Label>
            <Input id="fin-oblig" type="number" step="1" min="0" value={obligations} onChange={(e) => setObligations(e.target.value)} />
          </div>
          <div className="molecule-field" style={{ marginBottom: 6 }}>
            <Label htmlFor="fin-pay">Pagos</Label>
            <Input id="fin-pay" type="number" step="1" min="0" value={payments} onChange={(e) => setPayments(e.target.value)} />
          </div>

          <div className="modal-actions">
            <Button type="button" variant="ghost" onClick={() => { resetForm(); onClose(); }}>Cancelar</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</Button>
          </div>
        </form>
      </div>
    </div>
  );
}
