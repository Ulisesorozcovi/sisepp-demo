import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { StrategicLinesPanel } from '../design-system/organisms/StrategicLinesPanel';
import { ProgramsPanel } from '../design-system/organisms/ProgramsPanel';
import { ProjectsPanel } from '../design-system/organisms/ProjectsPanel';
import { ActivitiesPanel } from '../design-system/organisms/ActivitiesPanel';
import { PacTreeTable } from '../design-system/organisms/PacTreeTable';
import { DetailPanel } from '../design-system/organisms/DetailPanel';
import type { Activity, Program, Project, StrategicLine, TreeNode } from '../types';

type ViewMode = 'tree' | 'columns';

export function StructurePage() {
  const [viewMode, setViewMode] = useState<ViewMode>('tree');
  const [selectedLine, setSelectedLine] = useState<StrategicLine | null>(null);
  const [selectedProgram, setSelectedProgram] = useState<Program | null>(null);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [detailNode, setDetailNode] = useState<TreeNode | null>(null);

  function bump() {
    setRefreshKey((k) => k + 1);
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Estructura del PAC</h1>
          <p>Línea Estratégica → Programa → Proyecto → Actividad</p>
        </div>
        <div style={{ display: 'flex', gap: 6 }}>
          <button
            className={`atom-btn ${viewMode === 'tree' ? 'atom-btn--primary' : 'atom-btn--ghost'}`}
            onClick={() => setViewMode('tree')}
            style={{ padding: '8px 14px', fontSize: 13 }}
          >
            Árbol
          </button>
          <button
            className={`atom-btn ${viewMode === 'columns' ? 'atom-btn--primary' : 'atom-btn--ghost'}`}
            onClick={() => setViewMode('columns')}
            style={{ padding: '8px 14px', fontSize: 13 }}
          >
            Columnas
          </button>
        </div>
      </div>

      {viewMode === 'tree' && (
        <PacTreeTable refreshKey={refreshKey} onSelect={setDetailNode} />
      )}

      {detailNode && (
        <DetailPanel
          node={detailNode}
          onClose={() => setDetailNode(null)}
          onSaved={() => { bump(); setDetailNode(null); }}
        />
      )}

      {viewMode === 'columns' && (
        <div className="structure-columns">
          <StrategicLinesPanel
            selectedId={selectedLine?.id ?? null}
            onSelect={(line) => {
              setSelectedLine(line);
              setSelectedProgram(null);
              setSelectedProject(null);
              setSelectedActivity(null);
            }}
            refreshKey={refreshKey}
            onCreated={bump}
          />
          <ProgramsPanel
            strategicLineId={selectedLine?.id ?? null}
            selectedId={selectedProgram?.id ?? null}
            onSelect={(program) => {
              setSelectedProgram(program);
              setSelectedProject(null);
              setSelectedActivity(null);
            }}
            refreshKey={refreshKey}
            onCreated={bump}
          />
          <ProjectsPanel
            programId={selectedProgram?.id ?? null}
            selectedId={selectedProject?.id ?? null}
            onSelect={(project) => {
              setSelectedProject(project);
              setSelectedActivity(null);
            }}
            refreshKey={refreshKey}
            onCreated={bump}
          />
          <ActivitiesPanel
            projectId={selectedProject?.id ?? null}
            selectedId={selectedActivity?.id ?? null}
            onSelect={setSelectedActivity}
            refreshKey={refreshKey}
            onCreated={bump}
          />
        </div>
      )}
    </DashboardLayout>
  );
}
