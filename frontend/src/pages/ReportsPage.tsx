import { useEffect, useRef, useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { Select } from '../design-system/atoms/Select';
import { Button } from '../design-system/atoms/Button';
import { Icon } from '../design-system/atoms/Icon';
import { planningApi } from '../api/planningApi';
import { reportApi } from '../api/reportApi';
import { getApiErrorMessage } from '../utils/apiError';
import { formatCOP, formatPercent, formatNumber } from '../utils/format';
import { exportToCsv, exportToExcel, exportToPdf } from '../utils/exportReport';
import type { PlanningInstrument, PacMatrixResponse } from '../types';

type ReportTab = 'goals' | 'budgets';

export function ReportsPage() {
  const [pacs, setPacs] = useState<PlanningInstrument[]>([]);
  const [selectedPacId, setSelectedPacId] = useState('');
  const [report, setReport] = useState<PacMatrixResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<ReportTab>('goals');
  const [exportOpen, setExportOpen] = useState(false);
  const [exporting, setExporting] = useState(false);
  const exportRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (exportRef.current && !exportRef.current.contains(e.target as Node)) {
        setExportOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  async function handleExport(format: 'csv' | 'xlsx' | 'pdf') {
    if (!report) return;
    setExporting(true);
    setExportOpen(false);
    try {
      if (format === 'csv') exportToCsv(report, tab);
      else if (format === 'xlsx') await exportToExcel(report, tab);
      else await exportToPdf(report, tab);
    } finally {
      setExporting(false);
    }
  }

  useEffect(() => {
    planningApi.list({ type: 'PAC' }).then((list) => {
      setPacs(list);
      if (list.length > 0) setSelectedPacId(list[0].id);
    });
  }, []);

  async function handleGenerate() {
    if (!selectedPacId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await reportApi.getPacMatrix({ pacId: selectedPacId });
      setReport(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo generar el reporte'));
      setReport(null);
    } finally {
      setLoading(false);
    }
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Reportes</h1>
          <p>Genera y descarga la matriz del PAC con estructura, metas y presupuesto.</p>
        </div>
      </div>

      <div className="organism-table-card">
        <div className="report-controls">
          <div>
            <label className="report-controls-label">PAC</label>
            <Select
              value={selectedPacId}
              onChange={(e) => setSelectedPacId(e.target.value)}
              style={{ minWidth: 220 }}
            >
              {pacs.length === 0 && <option value="">Sin PAC disponibles</option>}
              {pacs.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.startYear}–{p.endYear})
                </option>
              ))}
            </Select>
          </div>
          <Button onClick={handleGenerate} disabled={loading || !selectedPacId}>
            {loading ? 'Generando...' : 'Generar reporte'}
          </Button>
          {report && (
            <div className="export-dropdown" ref={exportRef}>
              <Button
                variant="ghost"
                onClick={() => setExportOpen((o) => !o)}
                disabled={exporting}
              >
                {exporting ? 'Exportando...' : 'Descargar'}
                <Icon name="chevron-down" size={14} />
              </Button>
              {exportOpen && (
                <ul className="export-dropdown__menu">
                  <li>
                    <button onClick={() => handleExport('csv')}>
                      CSV (.csv)
                    </button>
                  </li>
                  <li>
                    <button onClick={() => handleExport('xlsx')}>
                      Excel (.xlsx)
                    </button>
                  </li>
                  <li>
                    <button onClick={() => handleExport('pdf')}>
                      PDF (.pdf)
                    </button>
                  </li>
                </ul>
              )}
            </div>
          )}
        </div>

        {error && (
          <div className="molecule-field-error" style={{ padding: '0 16px 12px' }}>
            {error}
          </div>
        )}

        {report && (
          <>
            <div className="report-tabs">
              <button
                className={`report-tab ${tab === 'goals' ? 'report-tab--active' : ''}`}
                onClick={() => setTab('goals')}
              >
                Metas físicas
              </button>
              <button
                className={`report-tab ${tab === 'budgets' ? 'report-tab--active' : ''}`}
                onClick={() => setTab('budgets')}
              >
                Recursos financieros
              </button>
            </div>

            <div className="pac-matrix-container">
              <table className="pac-matrix">
                <thead>
                  <tr>
                    <th>Línea Estratégica</th>
                    <th className="weight">%</th>
                    <th>Programa</th>
                    <th className="weight">%</th>
                    <th>Proyecto</th>
                    <th className="weight">%</th>
                    <th>PIP</th>
                    <th>Objetivo</th>
                    <th className="weight">Obj %</th>
                    <th>Actividad</th>
                    <th className="weight">%</th>
                    <th>Indicador</th>
                    <th>Categoría</th>
                    <th>Ud. Medida</th>
                    {report.years.map((y) => (
                      <th key={y} className="numeric">
                        {y}
                      </th>
                    ))}
                    <th className="numeric">Total</th>
                  </tr>
                </thead>
                <tbody>
                  {report.strategicLines.map((line) =>
                    line.programs.map((prog) =>
                      prog.projects.map((proj) => {
                        const actRows = proj.activities.map((act) => {
                          const yearValues = report.years.map((y) =>
                            tab === 'goals' ? act.goals[y] : act.budgets[y],
                          );
                          const total = yearValues.reduce(
                            (s, v) => s + (v ?? 0),
                            0,
                          );
                          return (
                            <tr key={act.id} data-level="activity">
                              <td>{line.name}</td>
                              <td className="weight">
                                {formatPercent(line.weight)}
                              </td>
                              <td>
                                {prog.code ? `[${prog.code}] ` : ''}
                                {prog.name}
                              </td>
                              <td className="weight">
                                {formatPercent(prog.weight)}
                              </td>
                              <td>{proj.name}</td>
                              <td className="weight">
                                {formatPercent(proj.weight)}
                              </td>
                              <td>{proj.pip ?? ''}</td>
                              <td>{proj.objective ?? ''}</td>
                              <td className="weight">
                                {proj.objectiveWeight != null
                                  ? formatPercent(proj.objectiveWeight)
                                  : ''}
                              </td>
                              <td>{act.name}</td>
                              <td className="weight">
                                {formatPercent(act.weight)}
                              </td>
                              <td>{act.indicator ?? ''}</td>
                              <td>{act.category ?? ''}</td>
                              <td>{act.measurementUnit ?? ''}</td>
                              {yearValues.map((v, i) => (
                                <td key={report.years[i]} className="numeric">
                                  {v != null
                                    ? tab === 'budgets'
                                      ? formatCOP(v)
                                      : formatNumber(v)
                                    : ''}
                                </td>
                              ))}
                              <td className="numeric total-cell">
                                {tab === 'budgets'
                                  ? formatCOP(total)
                                  : formatNumber(total)}
                              </td>
                            </tr>
                          );
                        });

                        const subtotals = report.years.map((y) =>
                          proj.activities.reduce(
                            (s, a) =>
                              s +
                              (tab === 'goals'
                                ? (a.goals[y] ?? 0)
                                : (a.budgets[y] ?? 0)),
                            0,
                          ),
                        );
                        const subtotalTotal = subtotals.reduce(
                          (s, v) => s + v,
                          0,
                        );

                        return [
                          ...actRows,
                          proj.activities.length > 0 && (
                            <tr
                              key={`sub-${proj.id}`}
                              className="pac-matrix__subtotal"
                            >
                              <td colSpan={14} style={{ textAlign: 'right' }}>
                                Subtotal {proj.name}
                              </td>
                              {subtotals.map((v, i) => (
                                <td key={report.years[i]} className="numeric">
                                  {tab === 'budgets'
                                    ? formatCOP(v)
                                    : formatNumber(v)}
                                </td>
                              ))}
                              <td className="numeric total-cell">
                                {tab === 'budgets'
                                  ? formatCOP(subtotalTotal)
                                  : formatNumber(subtotalTotal)}
                              </td>
                            </tr>
                          ),
                        ];
                      }),
                    ),
                  )}
                  {(() => {
                    const allActivities = report.strategicLines.flatMap((l) =>
                      l.programs.flatMap((p) => p.projects.flatMap((pr) => pr.activities)),
                    );
                    const grandTotals = report.years.map((y) =>
                      allActivities.reduce(
                        (s, a) => s + ((tab === 'goals' ? a.goals[y] : a.budgets[y]) ?? 0),
                        0,
                      ),
                    );
                    const grandSum = grandTotals.reduce((s, v) => s + v, 0);
                    return (
                      <tr className="pac-matrix__total">
                        <td colSpan={14}>
                          <strong>TOTAL GENERAL</strong>
                        </td>
                        {grandTotals.map((v, i) => (
                          <td key={report.years[i]} className="numeric">
                            {tab === 'budgets' ? formatCOP(v) : formatNumber(v)}
                          </td>
                        ))}
                        <td className="numeric">
                          {tab === 'budgets' ? formatCOP(grandSum) : formatNumber(grandSum)}
                        </td>
                      </tr>
                    );
                  })()}
                </tbody>
              </table>
            </div>
          </>
        )}

        {!report && !loading && !error && (
          <div className="empty-state" style={{ padding: 32 }}>
            Selecciona un PAC y haz clic en "Generar reporte" para ver la
            matriz.
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
