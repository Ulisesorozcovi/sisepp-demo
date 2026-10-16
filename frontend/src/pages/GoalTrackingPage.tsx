import { useEffect, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { Label } from '../design-system/atoms/Label';
import { Select } from '../design-system/atoms/Select';
import { Button } from '../design-system/atoms/Button';
import { projectApi, activityApi } from '../api/structureApi';
import { taskApi } from '../api/taskApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { Project, Activity, TaskWithProgress } from '../types';

const STATUS_LABELS: Record<string, string> = {
  EN_PROCESO: 'En proceso',
  CUMPLIDA: 'Cumplida',
  REZAGADA: 'Rezagada',
  NO_APLICA: 'No aplica',
};

function completionBadgeClass(pct: number): string {
  if (pct >= 100) return 'goal-badge goal-badge--complete';
  if (pct >= 60) return 'goal-badge goal-badge--mid';
  if (pct > 0) return 'goal-badge goal-badge--low';
  return 'goal-badge goal-badge--zero';
}

export function GoalTrackingPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [tasks, setTasks] = useState<TaskWithProgress[]>([]);

  const [projectId, setProjectId] = useState('');
  const [activityId, setActivityId] = useState('');

  const [loadingProjects, setLoadingProjects] = useState(true);
  const [loadingActivities, setLoadingActivities] = useState(false);
  const [loadingTasks, setLoadingTasks] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    projectApi.list({}).then((data) => {
      setProjects(data);
      setLoadingProjects(false);
    }).catch(() => setLoadingProjects(false));
  }, []);

  useEffect(() => {
    if (!projectId) {
      setActivities([]);
      setActivityId('');
      setTasks([]);
      return;
    }
    setLoadingActivities(true);
    setActivityId('');
    setTasks([]);
    activityApi.list(projectId).then((data) => {
      setActivities(data);
      setLoadingActivities(false);
    }).catch(() => setLoadingActivities(false));
  }, [projectId]);

  useEffect(() => {
    if (!activityId) {
      setTasks([]);
      return;
    }
    setLoadingTasks(true);
    setError(null);
    taskApi.listWithProgress(activityId).then((data) => {
      setTasks(data);
      setLoadingTasks(false);
    }).catch((err) => {
      setError(getApiErrorMessage(err, 'No se pudieron cargar las tareas'));
      setLoadingTasks(false);
    });
  }, [activityId]);

  const selectedActivity = activities.find((a) => a.id === activityId);

  const totalGoal = tasks.reduce((sum, t) => sum + (t.physicalGoalValue ?? 0), 0);
  const totalExecuted = tasks.reduce((sum, t) => sum + (t.executedAccumulated ?? 0), 0);
  const totalPct = totalGoal > 0 ? Math.min(100, (totalExecuted / totalGoal) * 100) : 0;

  const [printing, setPrinting] = useState(false);

  async function handlePrint() {
    const element = document.getElementById('goal-tracking-table');
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
      const selectedAct = activities.find((a) => a.id === activityId);
      pdf.setFontSize(14);
      pdf.text('Balance Comparativo: Programación vs. Ejecución Real', 14, 15);
      pdf.setFontSize(10);
      pdf.text(`Proyecto: ${selectedProj?.name ?? ''}`, 14, 22);
      pdf.text(`Actividad: ${selectedAct?.name ?? ''}`, 14, 28);
      pdf.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 34);
      const imgWidth = pageWidth - 28;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      const pageHeight = pdf.internal.pageSize.getHeight();
      pdf.addImage(imgData, 'PNG', 14, 40, imgWidth, Math.min(imgHeight, pageHeight - 50));
      pdf.save(`Consulta_Metas_${new Date().toISOString().slice(0, 10)}.pdf`);
    } finally {
      setPrinting(false);
    }
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Consulta de Metas</h1>
          <p>Balance comparativo: meta programada vs. avance ejecutado por tarea.</p>
        </div>
        {tasks.length > 0 && (
          <Button variant="outline" onClick={handlePrint} disabled={printing}>
            {printing ? 'Generando...' : 'Imprimir Reporte'}
          </Button>
        )}
      </div>

      <div className="organism-table-card">
        <div className="task-filters">
          <div className="molecule-field" style={{ minWidth: 240, flex: 1 }}>
            <Label htmlFor="gt-project">Proyecto</Label>
            <Select
              id="gt-project"
              value={projectId}
              onChange={(e) => setProjectId(e.target.value)}
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

          <div className="molecule-field" style={{ minWidth: 240, flex: 1 }}>
            <Label htmlFor="gt-activity">Actividad</Label>
            <Select
              id="gt-activity"
              value={activityId}
              onChange={(e) => setActivityId(e.target.value)}
              disabled={!projectId || loadingActivities}
            >
              <option value="">
                {!projectId
                  ? '— Seleccione un proyecto primero —'
                  : loadingActivities
                    ? 'Cargando...'
                    : '— Seleccione una actividad —'}
              </option>
              {activities.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.code ? `${a.code} — ` : ''}{a.name}
                </option>
              ))}
            </Select>
          </div>
        </div>

        {selectedActivity && selectedActivity.goal != null && (
          <div className="goal-activity-summary">
            <span><strong>Meta global de actividad:</strong> {selectedActivity.goal} {selectedActivity.measurementUnit ?? ''}</span>
          </div>
        )}

        {error && <div className="molecule-alert">{error}</div>}
        {loadingTasks && <div className="empty-state">Cargando tareas...</div>}

        {!loadingTasks && activityId && tasks.length === 0 && !error && (
          <div className="empty-state">No hay tareas registradas para esta actividad.</div>
        )}

        {tasks.length > 0 && (
          <table id="goal-tracking-table" className="users-table">
            <thead>
              <tr>
                <th>Tarea</th>
                <th>Responsable</th>
                <th>Meta programada</th>
                <th>Ejecutado acumulado</th>
                <th style={{ textAlign: 'center' }}>% Cumplimiento</th>
                <th>Último estado</th>
                <th>Último reporte</th>
              </tr>
            </thead>
            <tbody>
              {tasks.map((t) => (
                <tr key={t.id}>
                  <td>{t.name}</td>
                  <td>{t.responsibleName}</td>
                  <td>{t.physicalGoalValue} {t.physicalGoalUnit}</td>
                  <td>{t.executedAccumulated}</td>
                  <td style={{ textAlign: 'center' }}>
                    <span className={completionBadgeClass(t.completionPercent)}>
                      {t.completionPercent.toFixed(1)}%
                    </span>
                  </td>
                  <td>
                    {t.lastFollowUpStatus ? (
                      <span className={`followup-status-pill followup-status-pill--${t.lastFollowUpStatus.toLowerCase()}`}>
                        {STATUS_LABELS[t.lastFollowUpStatus] ?? t.lastFollowUpStatus}
                      </span>
                    ) : '—'}
                  </td>
                  <td style={{ fontSize: 12, whiteSpace: 'nowrap' }}>
                    {t.lastFollowUpDate
                      ? new Date(t.lastFollowUpDate).toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
                      : '—'}
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr style={{ fontWeight: 600 }}>
                <td colSpan={2}>TOTAL</td>
                <td>{totalGoal}</td>
                <td>{totalExecuted}</td>
                <td style={{ textAlign: 'center' }}>
                  <span className={completionBadgeClass(totalPct)}>
                    {totalPct.toFixed(1)}%
                  </span>
                </td>
                <td colSpan={2}></td>
              </tr>
            </tfoot>
          </table>
        )}
      </div>
    </DashboardLayout>
  );
}
