import { useEffect, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { Label } from '../design-system/atoms/Label';
import { Select } from '../design-system/atoms/Select';
import { Button } from '../design-system/atoms/Button';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { financialApi } from '../api/financialApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { Project, Activity, TaskWithProgress, FinancialExecution } from '../types';

interface ConsolidatedRow {
  level: 'project' | 'activity' | 'task';
  name: string;
  code?: string | null;
  weight?: number | null;
  goalValue?: number;
  executed?: number;
  completionPct?: number;
  approvedBudget?: number;
  commitmentsPct?: number;
  obligationsPct?: number;
}

const COP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

export function ConsolidatedReportPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectId, setProjectId] = useState('');
  const [loadingProjects, setLoadingProjects] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [printing, setPrinting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [rows, setRows] = useState<ConsolidatedRow[]>([]);

  useEffect(() => {
    projectApi.list({}).then((data) => {
      setProjects(data);
      setLoadingProjects(false);
    }).catch(() => setLoadingProjects(false));
  }, []);

  async function handleGenerate() {
    if (!projectId) return;
    setGenerating(true);
    setError(null);
    setRows([]);
    try {
      const [activities, financials] = await Promise.all([
        activityApi.list(projectId),
        financialApi.list({ projectId }),
      ]);

      const tasksMap = new Map<string, TaskWithProgress[]>();
      await Promise.all(
        activities.map((act) =>
          taskApi.listWithProgress(act.id).then((t) => tasksMap.set(act.id, t)).catch(() => tasksMap.set(act.id, [])),
        ),
      );

      const project = projects.find((p) => p.id === projectId);
      const totalBudget = financials.reduce((s, f) => s + f.approvedBudget, 0);
      const totalCommitments = financials.reduce((s, f) => s + f.commitments, 0);
      const totalObligations = financials.reduce((s, f) => s + f.obligations, 0);

      const built: ConsolidatedRow[] = [];

      built.push({
        level: 'project',
        name: project?.name ?? '',
        code: project?.code,
        approvedBudget: totalBudget,
        commitmentsPct: totalBudget > 0 ? (totalCommitments / totalBudget) * 100 : 0,
        obligationsPct: totalBudget > 0 ? (totalObligations / totalBudget) * 100 : 0,
      });

      for (const act of activities) {
        const actTasks = tasksMap.get(act.id) ?? [];
        const actGoal = act.goal ?? 0;
        const actExecuted = actTasks.reduce((s, t) => s + (t.executedAccumulated ?? 0), 0);
        const actPct = actGoal > 0 ? Math.min(100, (actExecuted / actGoal) * 100) : 0;

        built.push({
          level: 'activity',
          name: act.name,
          code: act.code,
          goalValue: actGoal,
          executed: actExecuted,
          completionPct: actPct,
        });

        for (const task of actTasks) {
          built.push({
            level: 'task',
            name: task.name,
            weight: task.physicalGoalValue,
            goalValue: task.physicalGoalValue,
            executed: task.executedAccumulated,
            completionPct: task.completionPercent,
          });
        }
      }

      setRows(built);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Error al generar el consolidado'));
    } finally {
      setGenerating(false);
    }
  }

  async function handlePrint() {
    const element = document.getElementById('consolidated-report-table');
    if (!element) return;
    setPrinting(true);
    try {
      const html2canvas = (await import('html2canvas')).default;
      const { jsPDF } = await import('jspdf');
      const canvas = await html2canvas(element, { scale: 2, useCORS: true, logging: false });
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('landscape', 'mm', 'letter');
      const pageWidth = pdf.internal.pageSize.getWidth();
      const selectedProj = projects.find((p) => p.id === projectId);
      pdf.setFontSize(14);
      pdf.text('Reporte Consolidado por Proyecto y Actividad', 14, 15);
      pdf.setFontSize(10);
      pdf.text(`Proyecto: ${selectedProj?.name ?? ''}`, 14, 22);
      pdf.text(`Generado: ${new Date().toLocaleDateString('es-CO')}`, 14, 28);
      const imgWidth = pageWidth - 28;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      const pageHeight = pdf.internal.pageSize.getHeight();
      pdf.addImage(imgData, 'PNG', 14, 34, imgWidth, Math.min(imgHeight, pageHeight - 44));
      pdf.save(`Consolidado_${selectedProj?.code ?? 'proyecto'}_${new Date().toISOString().slice(0, 10)}.pdf`);
    } finally {
      setPrinting(false);
    }
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Reporte Consolidado por Proyecto y Actividad</h1>
          <p>Detalle operativo de actividades, tareas y ejecuci&oacute;n financiera.</p>
        </div>
      </div>

      <div className="organism-table-card">
        <div className="task-filters">
          <div className="molecule-field" style={{ minWidth: 280, flex: 1 }}>
            <Label htmlFor="cr-project">Proyecto asignado *</Label>
            <Select
              id="cr-project"
              value={projectId}
              onChange={(e) => { setProjectId(e.target.value); setRows([]); }}
              disabled={loadingProjects}
            >
              <option value="">
                {loadingProjects ? 'Cargando...' : '— Seleccione un proyecto —'}
              </option>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.code ? `${p.code} — ` : ''}{p.name}
                </option>
              ))}
            </Select>
          </div>

          <Button onClick={handleGenerate} disabled={!projectId || generating} style={{ alignSelf: 'flex-end' }}>
            {generating ? 'Generando...' : 'Generar Consolidado de Inversión'}
          </Button>
        </div>

        {error && <div className="molecule-alert">{error}</div>}

        {rows.length > 0 && (
          <>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 20px 0' }}>
              <h3 style={{ margin: 0, fontSize: 14, fontWeight: 600, color: 'var(--blue-900)' }}>
                Detalle Operativo de Actividades y Tareas
              </h3>
              <Button variant="outline" onClick={handlePrint} disabled={printing}>
                {printing ? 'Generando PDF...' : 'Imprimir'}
              </Button>
            </div>

            <div style={{ overflowX: 'auto', padding: '0 20px 20px' }}>
              <table id="consolidated-report-table" className="consolidated-table">
                <thead>
                  <tr>
                    <th>Nivel / Descripci&oacute;n</th>
                    <th>Meta</th>
                    <th>Ejecutado</th>
                    <th>% Cumplimiento</th>
                    <th>Presupuesto ($M)</th>
                    <th>% Compromisos</th>
                    <th>% Obligaciones</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row, i) => (
                    <tr key={i} data-level={row.level}>
                      <td>
                        {row.level === 'project' && (
                          <div>
                            <strong>{'\u{1F4C1}'} Proyecto: [{row.code ?? ''}]</strong>
                            <div style={{ fontSize: 12, color: 'var(--ink-600)' }}>{row.name}</div>
                          </div>
                        )}
                        {row.level === 'activity' && (
                          <div style={{ paddingLeft: 16 }}>
                            <strong>{'≡'} Actividad:</strong>
                            <div style={{ fontSize: 12, color: 'var(--ink-600)' }}>
                              {row.name}
                              {row.code && <span style={{ marginLeft: 6, color: 'var(--ink-400)' }}>({row.code})</span>}
                            </div>
                          </div>
                        )}
                        {row.level === 'task' && (
                          <div style={{ paddingLeft: 32 }}>
                            {'✓'} Tarea: {row.name}
                            {row.weight != null && (
                              <span style={{ marginLeft: 8, fontSize: 11, color: 'var(--ink-500)' }}>[Peso: {row.weight}]</span>
                            )}
                          </div>
                        )}
                      </td>
                      <td>{row.goalValue != null ? row.goalValue.toFixed(2) : '—'}</td>
                      <td className={row.executed && row.executed > 0 ? 'value-executed' : ''}>
                        {row.executed != null ? row.executed.toFixed(2) : '—'}
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        {row.completionPct != null ? (
                          <span className={row.completionPct >= 100 ? 'goal-badge goal-badge--complete' : 'goal-badge goal-badge--mid'}>
                            {row.completionPct.toFixed(1)}%
                          </span>
                        ) : '—'}
                      </td>
                      <td className="value-budget">
                        {row.approvedBudget != null ? COP.format(row.approvedBudget) : '—'}
                      </td>
                      <td className="pct-commitment">
                        {row.commitmentsPct != null ? `${row.commitmentsPct.toFixed(1)}%` : '—'}
                      </td>
                      <td>
                        {row.obligationsPct != null ? `${row.obligationsPct.toFixed(1)}%` : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </DashboardLayout>
  );
}
