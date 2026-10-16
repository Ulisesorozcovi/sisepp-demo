import { useCallback, useEffect, useMemo, useState } from 'react';
import { structureTreeApi, strategicLineApi, programApi, projectApi, activityApi, sdgIndicatorApi } from '../../api/structureApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { TreeToolbar } from '../molecules/TreeToolbar';
import { TreeRow } from '../molecules/TreeRow';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';
import { SdgCheckboxGroup } from '../molecules/SdgCheckboxGroup';
import type { TreeLevel } from '../atoms/LevelIcon';
import type { StrategicLineNode, SdgIndicator, TreeNode } from '../../types';

interface Props {
  refreshKey: number;
  onSelect?: (node: TreeNode) => void;
}

type NodeId = string;

interface InlineForm {
  level: TreeLevel;
  parentId: string | null;
}

const ALL_LEVELS = new Set<TreeLevel>(['linea', 'programa', 'proyecto', 'actividad']);

export function PacTreeTable({ refreshKey, onSelect }: Props) {
  const currentYear = new Date().getFullYear();
  const vigenciaOptions = useMemo(
    () => Array.from({ length: 5 }, (_, i) => currentYear - 2 + i),
    [currentYear],
  );

  const [vigencia, setVigencia] = useState(currentYear);
  const [visibleLevels, setVisibleLevels] = useState<Set<TreeLevel>>(new Set(ALL_LEVELS));
  const [tree, setTree] = useState<StrategicLineNode[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [errorDetail, setErrorDetail] = useState<string | null>(null);
  const [expanded, setExpanded] = useState<Set<NodeId>>(new Set());
  const [inlineForm, setInlineForm] = useState<InlineForm | null>(null);
  const [formName, setFormName] = useState('');
  const [formCode, setFormCode] = useState('');
  const [formSdgIds, setFormSdgIds] = useState<number[]>([]);
  const [sdgOptions, setSdgOptions] = useState<SdgIndicator[]>([]);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [internalRefresh, setInternalRefresh] = useState(0);

  useEffect(() => {
    sdgIndicatorApi.list().then(setSdgOptions).catch(() => setSdgOptions([]));
  }, []);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    setErrorDetail(null);
    structureTreeApi
      .get(vigencia)
      .then((data) => {
        if (!active) return;
        setTree(data);
        const ids = new Set<NodeId>();
        data.forEach((l) => {
          ids.add(l.id);
          l.programs.forEach((p) => ids.add(p.id));
        });
        setExpanded(ids);
      })
      .catch((err) => {
        if (!active) return;
        const anyErr = err as any;
        const status = anyErr?.response?.status;
        const detail = status
          ? `HTTP ${status} — ${anyErr?.response?.data?.message ?? anyErr?.message ?? 'Error desconocido'}`
          : anyErr?.message ?? 'No se pudo conectar al servidor';
        setError('No se pudo cargar la estructura');
        setErrorDetail(detail);
      })
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [vigencia, refreshKey, internalRefresh]);

  const reload = useCallback(() => setInternalRefresh((k) => k + 1), []);

  const toggleLevel = useCallback((level: TreeLevel) => {
    setVisibleLevels((prev) => {
      const next = new Set(prev);
      if (next.has(level)) {
        if (next.size > 1) next.delete(level);
      } else {
        next.add(level);
      }
      return next;
    });
  }, []);

  const toggle = useCallback((id: NodeId) => {
    setExpanded((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }, []);

  const expandAll = useCallback(() => {
    const ids = new Set<NodeId>();
    tree.forEach((l) => {
      ids.add(l.id);
      l.programs.forEach((p) => {
        ids.add(p.id);
        p.projects.forEach((pj) => ids.add(pj.id));
      });
    });
    setExpanded(ids);
  }, [tree]);

  const collapseAll = useCallback(() => setExpanded(new Set()), []);

  function openInlineForm(level: TreeLevel, parentId: string | null = null) {
    setInlineForm({ level, parentId });
    setFormName('');
    setFormCode('');
    setFormSdgIds([]);
    setFormError(null);
  }

  function closeInlineForm() {
    setInlineForm(null);
    setFormName('');
    setFormCode('');
    setFormSdgIds([]);
    setFormError(null);
  }

  function handleAddItem(level: TreeLevel) {
    if (level === 'linea') {
      openInlineForm('linea', null);
    } else if (level === 'programa') {
      const firstLine = tree[0];
      if (firstLine) openInlineForm('programa', firstLine.id);
      else openInlineForm('linea', null);
    } else if (level === 'proyecto') {
      const firstProg = tree[0]?.programs[0];
      if (firstProg) openInlineForm('proyecto', firstProg.id);
      else openInlineForm('programa', tree[0]?.id ?? null);
    } else {
      const firstProj = tree[0]?.programs[0]?.projects[0];
      if (firstProj) openInlineForm('actividad', firstProj.id);
      else openInlineForm('proyecto', tree[0]?.programs[0]?.id ?? null);
    }
  }

  async function handleInlineCreate() {
    if (!inlineForm || !formName.trim()) return;
    if (inlineForm.level === 'actividad' && formSdgIds.length === 0) return;
    setSaving(true);
    setFormError(null);
    try {
      switch (inlineForm.level) {
        case 'linea':
          await strategicLineApi.create({ name: formName.trim() });
          break;
        case 'programa':
          if (!inlineForm.parentId) throw new Error('Seleccione una línea primero');
          await programApi.create({ name: formName.trim(), strategicLineId: inlineForm.parentId, code: formCode.trim() || undefined });
          break;
        case 'proyecto':
          if (!inlineForm.parentId) throw new Error('Seleccione un programa primero');
          await projectApi.create({ name: formName.trim(), programId: inlineForm.parentId, vigencia, code: formCode.trim() || undefined });
          break;
        case 'actividad':
          if (!inlineForm.parentId) throw new Error('Seleccione un proyecto primero');
          await activityApi.create({ name: formName.trim(), projectId: inlineForm.parentId, sdgIndicatorIds: formSdgIds, code: formCode.trim() || undefined });
          break;
      }
      closeInlineForm();
      reload();
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'No se pudo crear'));
    } finally {
      setSaving(false);
    }
  }

  function renderInlineFormRow(depth: number): JSX.Element {
    const form = inlineForm!;
    const indent = depth * 24;
    const levelLabel = form.level === 'linea' ? 'línea estratégica'
      : form.level === 'programa' ? 'programa'
      : form.level === 'proyecto' ? 'proyecto' : 'actividad';
    return (
      <tr key="__inline_form__">
        <td colSpan={5}>
          <div className="tree-inline-form" style={{ paddingLeft: indent + 48 }}>
            {formError && <div className="molecule-field-error" style={{ width: '100%', marginBottom: 4 }}>{formError}</div>}
            <Input
              placeholder={`Nombre de ${levelLabel}...`}
              value={formName}
              onChange={(e) => setFormName(e.target.value)}
              style={{ flex: 2, minWidth: 180 }}
              autoFocus
            />
            {form.level !== 'linea' && (
              <Input
                placeholder="Código"
                value={formCode}
                onChange={(e) => setFormCode(e.target.value)}
                style={{ width: 100 }}
              />
            )}
            <Button variant="ghost" onClick={handleInlineCreate} disabled={saving}>
              {saving ? '...' : 'Crear'}
            </Button>
            <Button variant="ghost" onClick={closeInlineForm}>Cancelar</Button>
          </div>
          {form.level === 'actividad' && (
            <div style={{ paddingLeft: indent + 48, paddingBottom: 8, paddingRight: 12 }}>
              <SdgCheckboxGroup options={sdgOptions} selectedIds={formSdgIds} onChange={setFormSdgIds} />
            </div>
          )}
        </td>
      </tr>
    );
  }

  const rows = useMemo(() => {
    const result: JSX.Element[] = [];
    let order = 0;

    const showLine = visibleLevels.has('linea');
    const showProg = visibleLevels.has('programa');
    const showProj = visibleLevels.has('proyecto');
    const showAct = visibleLevels.has('actividad');

    const progDepth = showLine ? 1 : 0;
    const projDepth = progDepth + (showProg ? 1 : 0);
    const actDepth = projDepth + (showProj ? 1 : 0);

    for (const line of tree) {
      if (showLine) {
        order++;
        const lineHasChildren = line.programs.length > 0 && showProg;
        result.push(
          <TreeRow
            key={line.id}
            order={order}
            level="linea"
            depth={0}
            name={line.name}
            status={line.status}
            hasChildren={lineHasChildren}
            expanded={expanded.has(line.id)}
            onToggle={() => toggle(line.id)}
            onAdd={() => openInlineForm('programa', line.id)}
            extra={line.description ? <span>{line.description}</span> : null}
            onClick={onSelect ? () => onSelect({ type: 'linea', id: line.id, name: line.name, description: line.description, status: line.status }) : undefined}
          />,
        );

        if (inlineForm?.level === 'programa' && inlineForm.parentId === line.id) {
          result.push(renderInlineFormRow(progDepth));
        }

        if (!expanded.has(line.id) && showProg) continue;
      }

      if (!showProg && !showProj && !showAct) continue;

      for (const prog of line.programs) {
        if (showProg) {
          order++;
          const progHasChildren = prog.projects.length > 0 && showProj;
          result.push(
            <TreeRow
              key={prog.id}
              order={order}
              level="programa"
              depth={progDepth}
              name={prog.name}
              code={prog.code}
              status={prog.status}
              hasChildren={progHasChildren}
              expanded={expanded.has(prog.id)}
              onToggle={() => toggle(prog.id)}
              onAdd={() => openInlineForm('proyecto', prog.id)}
              onClick={onSelect ? () => onSelect({ type: 'programa', id: prog.id, name: prog.name, code: prog.code, weight: prog.weight, description: prog.description, status: prog.status, strategicLineId: line.id }) : undefined}
            />,
          );

          if (inlineForm?.level === 'proyecto' && inlineForm.parentId === prog.id) {
            result.push(renderInlineFormRow(projDepth));
          }

          if (!expanded.has(prog.id) && showProj) continue;
        }

        if (!showProj && !showAct) continue;

        for (const proj of prog.projects) {
          if (showProj) {
            order++;
            const projHasChildren = proj.activities.length > 0 && showAct;
            result.push(
              <TreeRow
                key={proj.id}
                order={order}
                level="proyecto"
                depth={projDepth}
                name={proj.name}
                code={proj.code}
                status={proj.status}
                hasChildren={projHasChildren}
                expanded={expanded.has(proj.id)}
                onToggle={() => toggle(proj.id)}
                onAdd={() => openInlineForm('actividad', proj.id)}
                extra={
                  <>
                    <span>Vig. {proj.vigencia}</span>
                    {proj.coordinatorNames.map((cn, i) => (
                      <span className="tree-coordinator" key={i} style={{ marginLeft: 4 }}>{cn}</span>
                    ))}
                  </>
                }
                onClick={onSelect ? () => onSelect({ type: 'proyecto', id: proj.id, name: proj.name, code: proj.code, weight: proj.weight, subdivision: proj.subdivision, vigencia: proj.vigencia, coordinatorNames: proj.coordinatorNames, status: proj.status }) : undefined}
              />,
            );

            if (inlineForm?.level === 'actividad' && inlineForm.parentId === proj.id) {
              result.push(renderInlineFormRow(actDepth));
            }

            if (!expanded.has(proj.id) && showAct) continue;
          }

          if (!showAct) continue;

          for (const act of proj.activities) {
            order++;
            result.push(
              <TreeRow
                key={act.id}
                order={order}
                level="actividad"
                depth={actDepth}
                name={act.name}
                code={act.code}
                status={act.status}
                hasChildren={false}
                expanded={false}
                onToggle={() => {}}
                extra={act.measurementUnit ? <span>{act.measurementUnit}</span> : null}
                onClick={onSelect ? () => onSelect({ type: 'actividad', id: act.id, name: act.name, code: act.code, weight: act.weight, measurementUnit: act.measurementUnit, goal: act.goal, sdgIndicators: act.sdgIndicators, status: act.status }) : undefined}
              />,
            );
          }
        }
      }
    }

    return result;
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tree, expanded, visibleLevels, toggle, inlineForm, formName, formCode, formSdgIds, sdgOptions, saving, formError, onSelect]);

  return (
    <div className="organism-table-card">
      <TreeToolbar
        vigencia={vigencia}
        onVigenciaChange={setVigencia}
        vigenciaOptions={vigenciaOptions}
        visibleLevels={visibleLevels}
        onToggleLevel={toggleLevel}
        onExpandAll={expandAll}
        onCollapseAll={collapseAll}
        onAddItem={handleAddItem}
      />

      {loading && <div className="empty-state">Cargando estructura...</div>}

      {error && (
        <div className="empty-state" style={{ display: 'flex', flexDirection: 'column', gap: 6, alignItems: 'center' }}>
          <span>{error}</span>
          {errorDetail && <span style={{ fontSize: 11, color: 'var(--ink-400)' }}>{errorDetail}</span>}
          <button
            className="pac-tree-toolbar-btn"
            onClick={reload}
            style={{ marginTop: 6 }}
          >
            Reintentar
          </button>
        </div>
      )}

      {!loading && !error && (
        <div className="pac-tree-wrap">
          <table className="pac-tree-table">
            <thead>
              <tr>
                <th style={{ width: 40 }}>#</th>
                <th style={{ width: 100 }}>Tipo</th>
                <th>Título</th>
                <th style={{ width: 100 }}>Estado</th>
                <th>Detalle</th>
              </tr>
            </thead>
            <tbody>
              {inlineForm?.level === 'linea' && renderInlineFormRow(0)}
              {rows.length === 0 && !inlineForm && (
                <tr>
                  <td colSpan={5} className="tree-empty-row">
                    No hay elementos en la estructura para esta vigencia.
                  </td>
                </tr>
              )}
              {rows}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
