import { useEffect, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { Label } from '../design-system/atoms/Label';
import { Select } from '../design-system/atoms/Select';
import { Input } from '../design-system/atoms/Input';
import { Button } from '../design-system/atoms/Button';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { Project, Activity, TaskWithProgress } from '../types';

const CONDITION_OPTIONS = [
  { value: 'ALL', label: 'Todas las tareas' },
  { value: 'PENDING', label: 'Pendientes (No iniciadas)' },
  { value: 'IN_PROGRESS', label: 'En ejecución' },
  { value: 'COMPLETED', label: 'Completadas' },
  { value: 'OVERDUE', label: 'Retrasadas' },
] as const;

type ConditionValue = typeof CONDITION_OPTIONS[number]['value'];

function filterByCondition(tasks: TaskWithProgress[], condition: ConditionValue): TaskWithProgress[] {
  switch (condition) {
    case 'PENDING':
      return tasks.filter((t) => t.completionPercent === 0 && t.status === 'PROGRAMADA');
    case 'IN_PROGRESS':
      return tasks.filter((t) => t.completionPercent > 0 && t.completionPercent < 100);
    case 'COMPLETED':
      return tasks.filter((t) => t.completionPercent >= 100 || t.status === 'COMPLETADA');
    case 'OVERDUE':
      return tasks.filter((t) => {
        if (!t.dueDate) return false;
        return new Date(t.dueDate) < new Date() && t.status !== 'COMPLETADA';
      });
    default:
      return tasks;
  }
}

function formatDate(d: string | null): string {
  if (!d) return '—';
  return new Date(d + 'T00:00:00').toLocaleDateString('es-CO');
}

function getAdvanceBadgeClass(pct: number): string {
  if (pct >= 100) return 'tq-badge tq-badge--green';
  if (pct >= 70) return 'tq-badge tq-badge--orange';
  if (pct >= 30) return 'tq-badge tq-badge--blue';
  return 'tq-badge tq-badge--red';
}

const STATUS_LABELS: Record<string, string> = {
  EN_EJECUCION: 'En proceso',
  CUMPLIDA: 'Cumplida',
  EN_PROCESO: 'En proceso',
  REZAGADA: 'Rezagada',
  PROGRAMADA: 'Programada',
  COMPLETADA: 'Completada',
  RETRASADA: 'Retrasada',
  NO_APLICA: 'No aplica',
  CANCELADA: 'Cancelada',
};

function getStatusBadgeClass(status: string | null): string {
  if (!status) return 'tq-status tq-status--default';
  const s = status.toUpperCase();
  if (s === 'CUMPLIDA' || s === 'COMPLETADA') return 'tq-status tq-status--green';
  if (s === 'EN_PROCESO' || s === 'EN_EJECUCION') return 'tq-status tq-status--blue';
  if (s === 'REZAGADA' || s === 'RETRASADA') return 'tq-status tq-status--orange';
  return 'tq-status tq-status--default';
}

export function TaskQueryPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [loadingProjects, setLoadingProjects] = useState(true);
  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState('');
  const [condition, setCondition] = useState<ConditionValue>('ALL');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [tasks, setTasks] = useState<TaskWithProgress[]>([]);
  const [generating, setGenerating] = useState(false);
  const [printing, setPrinting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    projectApi.list({}).then((data) => {
      setProjects(data);
      setLoadingProjects(false);
    }).catch(() => setLoadingProjects(false));
  }, []);

  useEffect(() => {
    if (!projectId) { setActivities([]); return; }
    activityApi.list(projectId).then(setActivities).catch(() => setActivities([]));
  }, [projectId]);

  async function handleGenerate() {
    if (!projectId) return;
    setGenerating(true);
    setError(null);
    setTasks([]);
    try {
      let results: TaskWithProgress[];
      if (activityId) {
        results = await taskApi.listWithProgress(activityId);
      } else {
        results = await taskApi.listWithProgress(undefined, projectId);
      }

      if (dateFrom) {
        results = results.filter((t) => (t.dueDate ?? '') >= dateFrom);
      }
      if (dateTo) {
        results = results.filter((t) => (t.dueDate ?? '') <= dateTo);
      }

      results = filterByCondition(results, condition);
      setTasks(results);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Error al consultar tareas'));
    } finally {
      setGenerating(false);
    }
  }

  async function handlePrint() {
    const element = document.getElementById('task-query-table');
    if (!element) return;
    setPrinting(true);
    try {
      const html2canvas = (await import('html2canvas')).default;
      const { jsPDF } = await import('jspdf');
      const canvas = await html2canvas(element, { scale: 2, useCORS: true, logging: false });
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('landscape', 'mm', 'letter');
      const selectedProj = projects.find((p) => p.id === projectId);
      const condLabel = CONDITION_OPTIONS.find((c) => c.value === condition)?.label ?? '';
      pdf.setFontSize(14);
      pdf.text('Consulta de Tareas Ejecutadas / Pendientes', 14, 15);
      pdf.setFontSize(10);
      pdf.text(`Proyecto: ${selectedProj?.name ?? ''}`, 14, 22);
      pdf.text(`Condición: ${condLabel}`, 14, 28);
      pdf.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 34);
      const pageWidth = pdf.internal.pageSize.getWidth();
      const imgWidth = pageWidth - 28;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      const pageHeight = pdf.internal.pageSize.getHeight();
      pdf.addImage(imgData, 'PNG', 14, 40, imgWidth, Math.min(imgHeight, pageHeight - 50));
      pdf.save(`Consulta_Tareas_${condition}_${new Date().toISOString().slice(0, 10)}.pdf`);
    } finally {
      setPrinting(false);
    }
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Consulta de Tareas Ejecutadas / Pendientes</h1>
          <p>Filtra y consulta el estado de avance de las tareas por proyecto.</p>
        </div>
      </div>

      <div className="organism-table-card">
        <div className="task-filters">
          <div className="molecule-field" style={{ minWidth: 280, flex: 1 }}>
            <Label htmlFor="tq-project">Proyecto asignado *</Label>
            <Select
              id="tq-project"
              value={projectId}
              onChange={(e) => { setProjectId(e.target.value); setActivityId(''); setTasks([]); }}
              disabled={loadingProjects}
            >
              <option value="">{loadingProjects ? 'Cargando...' : '— Seleccione un proyecto —'}</option>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>{p.code ? `${p.code} — ` : ''}{p.name}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field" style={{ minWidth: 200, flex: 1 }}>
            <Label htmlFor="tq-activity">Actividad</Label>
            <Select
              id="tq-activity"
              value={activityId}
              onChange={(e) => { setActivityId(e.target.value); setTasks([]); }}
              disabled={!projectId}
            >
              <option value="">— Todas —</option>
              {activities.map((a) => (
                <option key={a.id} value={a.id}>{a.name}</option>
              ))}
            </Select>
          </div>

          <div className="molecule-field" style={{ minWidth: 180 }}>
            <Label htmlFor="tq-condition">Condición</Label>
            <Select
              id="tq-condition"
              value={condition}
              onChange={(e) => setCondition(e.target.value as ConditionValue)}
            >
              {CONDITION_OPTIONS.map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </Select>
          </div>
        </div>

        <div className="task-filters" style={{ paddingTop: 0 }}>
          <div className="molecule-field" style={{ minWidth: 160 }}>
            <Label htmlFor="tq-from">Desde (Opcional)</Label>
            <Input id="tq-from" type="date" value={dateFrom} onChange={(e) => setDateFrom(e.target.value)} />
          </div>
          <div className="molecule-field" style={{ minWidth: 160 }}>
            <Label htmlFor="tq-to">Hasta (Opcional)</Label>
            <Input id="tq-to" type="date" value={dateTo} onChange={(e) => setDateTo(e.target.value)} />
          </div>
          <Button onClick={handleGenerate} disabled={!projectId || generating} style={{ alignSelf: 'flex-end' }}>
            {generating ? 'Consultando...' : 'Generar Consulta de Tareas'}
          </Button>
        </div>

        {error && <div className="molecule-alert">{error}</div>}

        {tasks.length > 0 && (
          <>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 20px 0' }}>
              <h3 style={{ margin: 0, fontSize: 14, fontWeight: 600, color: 'var(--blue-900)' }}>
                Resultados obtenidos según los criterios ({tasks.length})
              </h3>
              <Button variant="outline" onClick={handlePrint} disabled={printing}>
                {printing ? 'Generando PDF...' : 'Imprimir'}
              </Button>
            </div>

            <div style={{ overflowX: 'auto', padding: '0 20px 20px' }}>
              <table id="task-query-table" className="task-query-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Descripción de la tarea</th>
                    <th>Actividad asociada</th>
                    <th>Responsable</th>
                    <th>Inicio Plan.</th>
                    <th>Fin Plan.</th>
                    <th style={{ textAlign: 'right' }}>Meta plan.</th>
                    <th style={{ textAlign: 'right' }}>Ejecutado acum.</th>
                    <th style={{ textAlign: 'center' }}>% Avance</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {tasks.map((t, i) => {
                    const displayStatus = t.lastFollowUpStatus ?? t.status ?? 'SIN_REPORTE';
                    return (
                      <tr key={t.id}>
                        <td><span className="task-query-table__number">{i + 1}</span></td>
                        <td>{t.name}</td>
                        <td style={{ fontSize: 12 }}>
                          <span style={{ color: 'var(--ink-500)' }}>Meta/Actividad: </span>
                          {t.activityName}
                        </td>
                        <td>{t.responsibleName}</td>
                        <td>{formatDate(t.startDate)}</td>
                        <td>{formatDate(t.dueDate)}</td>
                        <td style={{ textAlign: 'right' }}>{t.physicalGoalValue?.toFixed(2) ?? '—'}</td>
                        <td style={{ textAlign: 'right' }} className="value-executed">
                          {t.executedAccumulated?.toFixed(2) ?? '—'}
                        </td>
                        <td style={{ textAlign: 'center' }}>
                          <span className={getAdvanceBadgeClass(t.completionPercent)}>
                            {t.completionPercent.toFixed(1)}%
                          </span>
                        </td>
                        <td>
                          <span className={getStatusBadgeClass(displayStatus)}>
                            {STATUS_LABELS[displayStatus] ?? displayStatus}
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </>
        )}

        {tasks.length === 0 && !generating && !error && (
          <div className="empty-state" style={{ padding: 32 }}>
            Selecciona un proyecto y haz clic en "Generar Consulta de Tareas" para ver los resultados.
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
