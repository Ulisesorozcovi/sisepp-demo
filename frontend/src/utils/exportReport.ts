import type { PacMatrixResponse } from '../types';

type ReportTab = 'goals' | 'budgets';

function getHeaders(years: number[], tab: ReportTab): string[] {
  const yearLabels = tab === 'goals'
    ? years.map((y) => `Meta ${y}`)
    : years.map((y) => `Recurso ${y}`);
  return [
    'Línea Estratégica', 'Pond. Línea %',
    'Programa', 'Pond. Programa %',
    'Proyecto', 'Pond. Proyecto %',
    'PIP', 'Objetivo', 'Pond. Objetivo %',
    'Actividad', 'Pond. Actividad %',
    'Indicador', 'Categoría', 'Unidad de Medida',
    ...yearLabels,
    tab === 'goals' ? 'Total Metas' : 'Total Recursos',
  ];
}

function flattenRows(data: PacMatrixResponse, tab: ReportTab): string[][] {
  const { years } = data;
  const rows: string[][] = [];

  for (const line of data.strategicLines) {
    for (const prog of line.programs) {
      for (const proj of prog.projects) {
        for (const act of proj.activities) {
          const values = tab === 'goals'
            ? years.map((y) => (act.goals[y] != null ? String(act.goals[y]) : ''))
            : years.map((y) => (act.budgets[y] != null ? String(act.budgets[y]) : ''));
          const total = String(
            years.reduce((s, y) => s + ((tab === 'goals' ? act.goals[y] : act.budgets[y]) ?? 0), 0),
          );

          rows.push([
            line.name,
            line.weight != null ? String(line.weight) : '',
            prog.code ? `[${prog.code}] ${prog.name}` : prog.name,
            prog.weight != null ? String(prog.weight) : '',
            proj.name,
            proj.weight != null ? String(proj.weight) : '',
            proj.pip ?? '',
            proj.objective ?? '',
            proj.objectiveWeight != null ? String(proj.objectiveWeight) : '',
            act.name,
            act.weight != null ? String(act.weight) : '',
            act.indicator ?? '',
            act.category ?? '',
            act.measurementUnit ?? '',
            ...values,
            total,
          ]);
        }
      }
    }
  }
  return rows;
}

function buildFilename(tab: ReportTab, ext: string): string {
  const label = tab === 'goals' ? 'Metas' : 'Recursos';
  const date = new Date().toISOString().slice(0, 10);
  return `Matriz_PAC_${label}_${date}.${ext}`;
}

export function exportToCsv(data: PacMatrixResponse, tab: ReportTab): void {
  const headers = getHeaders(data.years, tab);
  const rows = flattenRows(data, tab);
  const csv =
    '﻿' +
    [headers, ...rows]
      .map((r) => r.map((c) => `"${c.replace(/"/g, '""')}"`).join(';'))
      .join('\n');

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  triggerDownload(blob, buildFilename(tab, 'csv'));
}

export async function exportToExcel(data: PacMatrixResponse, tab: ReportTab): Promise<void> {
  const XLSX = await import('xlsx');
  const headers = getHeaders(data.years, tab);
  const rows = flattenRows(data, tab);

  const ws = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const colWidths = headers.map((h, i) => {
    const maxLen = Math.max(h.length, ...rows.map((r) => (r[i] ?? '').length));
    return { wch: Math.min(maxLen + 2, 40) };
  });
  ws['!cols'] = colWidths;

  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, tab === 'goals' ? 'Metas' : 'Recursos');

  const buf = XLSX.write(wb, { bookType: 'xlsx', type: 'array' }) as ArrayBuffer;
  const blob = new Blob([buf], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  triggerDownload(blob, buildFilename(tab, 'xlsx'));
}

export async function exportToPdf(data: PacMatrixResponse, tab: ReportTab): Promise<void> {
  const { jsPDF } = await import('jspdf');
  const autoTableModule = await import('jspdf-autotable');
  const autoTable = autoTableModule.default;

  const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'legal' });

  doc.setFontSize(14);
  doc.text(`Matriz PAC — ${tab === 'goals' ? 'Metas Físicas' : 'Recursos Financieros'}`, 14, 15);

  doc.setFontSize(9);
  doc.text(`${data.pac.name} (${data.pac.startYear}–${data.pac.endYear})`, 14, 21);
  doc.text(`Generado: ${new Date().toLocaleDateString('es-CO')}`, 14, 26);

  const headers = getHeaders(data.years, tab);
  const rows = flattenRows(data, tab);

  autoTable(doc, {
    head: [headers],
    body: rows,
    startY: 30,
    styles: { fontSize: 5.5, cellPadding: 1.5 },
    headStyles: { fillColor: [45, 47, 110], fontSize: 5.5, halign: 'center' },
    columnStyles: {
      1: { halign: 'center' },
      3: { halign: 'center' },
      5: { halign: 'center' },
      8: { halign: 'center' },
      10: { halign: 'center' },
    },
    didParseCell(hookData) {
      const colCount = headers.length;
      const col = hookData.column.index;
      if (hookData.section === 'body' && col >= colCount - data.years.length - 1) {
        hookData.cell.styles.halign = 'right';
      }
    },
  });

  doc.save(buildFilename(tab, 'pdf'));
}

function triggerDownload(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}
