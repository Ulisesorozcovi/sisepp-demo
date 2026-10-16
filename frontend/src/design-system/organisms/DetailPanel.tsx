import { useEffect, useState } from 'react';
import { Icon } from '../atoms/Icon';
import { LevelIcon } from '../atoms/LevelIcon';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';
import { SdgCheckboxGroup } from '../molecules/SdgCheckboxGroup';
import { Select } from '../atoms/Select';
import { strategicLineApi, programApi, projectApi, activityApi, sdgIndicatorApi } from '../../api/structureApi';
import { catalogApi } from '../../api/catalogApi';
import { planningApi } from '../../api/planningApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { useAuth } from '../../context/AuthContext';
import type {
  TreeNode,
  LineNode,
  ProgramNodeDetail,
  ProjectNodeDetail,
  ActivityNodeDetail,
  AnnualGoalEntry,
  AnnualBudgetEntry,
  SdgIndicator,
  MeasurementUnit,
  ThematicCategory,
  UserRole,
} from '../../types';

interface Props {
  node: TreeNode;
  onClose: () => void;
  onSaved: () => void;
}

const TYPE_LABELS = {
  linea: 'Línea Estratégica',
  programa: 'Programa',
  proyecto: 'Proyecto',
  actividad: 'Actividad',
} as const;

function canEdit(role: UserRole): boolean {
  return role === 'STAFF' || role === 'DIRECTOR';
}

export function DetailPanel({ node, onClose, onSaved }: Props) {
  const { user } = useAuth();
  const editable = user ? canEdit(user.role) : false;

  return (
    <div className="detail-panel-overlay">
      <div className="detail-panel-backdrop" onClick={onClose} />
      <div className="detail-panel">
        <div className="detail-panel__header">
          <div className="detail-panel__title">
            <LevelIcon level={node.type} />
            <span>{TYPE_LABELS[node.type]}</span>
          </div>
          <button className="detail-panel__close" onClick={onClose}>
            <Icon name="plus" size={18} />
          </button>
        </div>

        <h2 className="detail-panel__name">
          {node.code ? `[${node.code}] ` : ''}
          {node.name}
        </h2>
        <hr className="detail-panel__divider" />

        {node.type === 'linea' && (
          <LineDetail node={node} editable={editable} onSaved={onSaved} />
        )}
        {node.type === 'programa' && (
          <ProgramDetail node={node} editable={editable} onSaved={onSaved} />
        )}
        {node.type === 'proyecto' && (
          <ProjectDetail node={node} editable={editable} onSaved={onSaved} />
        )}
        {node.type === 'actividad' && (
          <ActivityDetail node={node} editable={editable} onSaved={onSaved} />
        )}
      </div>
    </div>
  );
}

function LineDetail({
  node,
  editable,
  onSaved,
}: {
  node: LineNode;
  editable: boolean;
  onSaved: () => void;
}) {
  const [name, setName] = useState(node.name);
  const [description, setDescription] = useState(node.description ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setName(node.name);
    setDescription(node.description ?? '');
  }, [node.id, node.name, node.description]);

  async function handleSave() {
    setSaving(true);
    setError(null);
    try {
      await strategicLineApi.update(node.id, {
        name: name.trim(),
        description: description.trim() || undefined,
      });
      onSaved();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo actualizar'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <Section title="Información general">
        <Field label="Nombre">
          {editable ? (
            <Input value={name} onChange={(e) => setName(e.target.value)} />
          ) : (
            <ReadOnly value={name} />
          )}
        </Field>
        <Field label="Descripción">
          {editable ? (
            <textarea
              className="detail-panel__textarea"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          ) : (
            <ReadOnly value={description || '—'} />
          )}
        </Field>
        <Field label="Estado">
          <ReadOnly value={node.status === 'ACTIVO' ? 'Activo' : 'Inactivo'} />
        </Field>
      </Section>

      {error && <div className="molecule-field-error">{error}</div>}
      {editable && (
        <Button onClick={handleSave} disabled={saving} style={{ marginTop: 16 }}>
          {saving ? 'Guardando...' : 'Guardar cambios'}
        </Button>
      )}
    </>
  );
}

function ProgramDetail({
  node,
  editable,
  onSaved,
}: {
  node: ProgramNodeDetail;
  editable: boolean;
  onSaved: () => void;
}) {
  const [name, setName] = useState(node.name);
  const [code, setCode] = useState(node.code ?? '');
  const [weight, setWeight] = useState(node.weight != null ? String(node.weight) : '');
  const [description, setDescription] = useState(node.description ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setName(node.name);
    setCode(node.code ?? '');
    setWeight(node.weight != null ? String(node.weight) : '');
    setDescription(node.description ?? '');
  }, [node.id, node.name, node.code, node.weight, node.description]);

  async function handleSave() {
    setSaving(true);
    setError(null);
    try {
      await programApi.update(node.id, {
        name: name.trim(),
        code: code.trim() || undefined,
        weight: weight ? Number(weight) : undefined,
        description: description.trim() || undefined,
      });
      onSaved();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo actualizar'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <Section title="Información general">
        <div className="detail-panel__row">
          <Field label="Código PAC">
            {editable ? (
              <Input value={code} onChange={(e) => setCode(e.target.value)} />
            ) : (
              <ReadOnly value={code || '—'} />
            )}
          </Field>
          <Field label="Ponderación (%)">
            {editable ? (
              <Input
                type="number"
                value={weight}
                onChange={(e) => setWeight(e.target.value)}
              />
            ) : (
              <ReadOnly value={weight ? `${weight}%` : '—'} />
            )}
          </Field>
        </div>
        <Field label="Nombre">
          {editable ? (
            <Input value={name} onChange={(e) => setName(e.target.value)} />
          ) : (
            <ReadOnly value={name} />
          )}
        </Field>
        <Field label="Objetivo">
          {editable ? (
            <textarea
              className="detail-panel__textarea"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          ) : (
            <ReadOnly value={description || '—'} />
          )}
        </Field>
      </Section>

      {error && <div className="molecule-field-error">{error}</div>}
      {editable && (
        <Button onClick={handleSave} disabled={saving} style={{ marginTop: 16 }}>
          {saving ? 'Guardando...' : 'Guardar cambios'}
        </Button>
      )}
    </>
  );
}

function ProjectDetail({
  node,
  editable,
  onSaved,
}: {
  node: ProjectNodeDetail;
  editable: boolean;
  onSaved: () => void;
}) {
  const [name, setName] = useState(node.name);
  const [code, setCode] = useState(node.code ?? '');
  const [weight, setWeight] = useState(node.weight != null ? String(node.weight) : '');
  const [vigencia, setVigencia] = useState(node.vigencia);
  const [subdivision, setSubdivision] = useState(node.subdivision ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setName(node.name);
    setCode(node.code ?? '');
    setWeight(node.weight != null ? String(node.weight) : '');
    setVigencia(node.vigencia);
    setSubdivision(node.subdivision ?? '');
  }, [node.id, node.name, node.code, node.weight, node.vigencia, node.subdivision]);

  async function handleSave() {
    setSaving(true);
    setError(null);
    try {
      await projectApi.update(node.id, {
        name: name.trim(),
        vigencia,
        code: code.trim() || undefined,
        subdivision: subdivision.trim() || undefined,
        weight: weight ? Number(weight) : undefined,
      });
      onSaved();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo actualizar'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <Section title="Información general">
        <div className="detail-panel__row">
          <Field label="Código PAC">
            {editable ? (
              <Input value={code} onChange={(e) => setCode(e.target.value)} />
            ) : (
              <ReadOnly value={code || '—'} />
            )}
          </Field>
          <Field label="Ponderación (%)">
            {editable ? (
              <Input
                type="number"
                value={weight}
                onChange={(e) => setWeight(e.target.value)}
              />
            ) : (
              <ReadOnly value={weight ? `${weight}%` : '—'} />
            )}
          </Field>
          <Field label="Vigencia">
            {editable ? (
              <Input
                type="number"
                value={String(vigencia)}
                onChange={(e) => setVigencia(Number(e.target.value))}
              />
            ) : (
              <ReadOnly value={String(vigencia)} />
            )}
          </Field>
        </div>
        <Field label="Nombre">
          {editable ? (
            <Input value={name} onChange={(e) => setName(e.target.value)} />
          ) : (
            <ReadOnly value={name} />
          )}
        </Field>
        <Field label="Subdirección">
          {editable ? (
            <Input value={subdivision} onChange={(e) => setSubdivision(e.target.value)} />
          ) : (
            <ReadOnly value={subdivision || '—'} />
          )}
        </Field>
      </Section>

      <Section title="Coordinadores asignados">
        {node.coordinatorNames.length === 0 ? (
          <p className="detail-panel__empty">Sin coordinadores asignados</p>
        ) : (
          <ul className="detail-panel__list">
            {node.coordinatorNames.map((cn, i) => (
              <li key={i}>{cn}</li>
            ))}
          </ul>
        )}
      </Section>

      {error && <div className="molecule-field-error">{error}</div>}
      {editable && (
        <Button onClick={handleSave} disabled={saving} style={{ marginTop: 16 }}>
          {saving ? 'Guardando...' : 'Guardar cambios'}
        </Button>
      )}
    </>
  );
}

function ActivityDetail({
  node,
  editable,
  onSaved,
}: {
  node: ActivityNodeDetail;
  editable: boolean;
  onSaved: () => void;
}) {
  const [name, setName] = useState(node.name);
  const [code, setCode] = useState(node.code ?? '');
  const [weight, setWeight] = useState(node.weight != null ? String(node.weight) : '');
  const [sdgIds, setSdgIds] = useState<number[]>(
    node.sdgIndicators.map((s) => s.id),
  );
  const [indicatorDescription, setIndicatorDescription] = useState(node.indicatorDescription ?? '');
  const [indicatorType, setIndicatorType] = useState(node.indicatorType ?? '');
  const [thematicCategoryId, setThematicCategoryId] = useState(node.thematicCategoryId ?? '');
  const [measurementUnitId, setMeasurementUnitId] = useState(node.measurementUnitId ?? '');
  const [sdgOptions, setSdgOptions] = useState<SdgIndicator[]>([]);
  const [measurementUnits, setMeasurementUnits] = useState<MeasurementUnit[]>([]);
  const [thematicCategories, setThematicCategories] = useState<ThematicCategory[]>([]);
  const [annualGoals, setAnnualGoals] = useState<AnnualGoalEntry[]>([]);
  const [annualBudgets, setAnnualBudgets] = useState<AnnualBudgetEntry[]>([]);
  const [pacYears, setPacYears] = useState<number[]>([]);
  const [loadingExtras, setLoadingExtras] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    sdgIndicatorApi.list().then(setSdgOptions).catch(() => setSdgOptions([]));
    catalogApi.listMeasurementUnits().then(setMeasurementUnits).catch(() => setMeasurementUnits([]));
    catalogApi.listThematicCategories().then(setThematicCategories).catch(() => setThematicCategories([]));
    planningApi.list({ type: 'PAC' }).then((pacs) => {
      const active = pacs.find((p) => p.status === 'ACTIVO') ?? pacs[0];
      if (active) {
        const years: number[] = [];
        for (let y = active.startYear; y <= active.endYear; y++) years.push(y);
        setPacYears(years);
      }
    }).catch(() => setPacYears([]));
  }, []);

  useEffect(() => {
    setName(node.name);
    setCode(node.code ?? '');
    setWeight(node.weight != null ? String(node.weight) : '');
    setSdgIds(node.sdgIndicators.map((s) => s.id));
    setIndicatorDescription(node.indicatorDescription ?? '');
    setIndicatorType(node.indicatorType ?? '');
    setThematicCategoryId(node.thematicCategoryId ?? '');
    setMeasurementUnitId(node.measurementUnitId ?? '');
    setLoadingExtras(true);
    activityApi.getById(node.id).then((full) => {
      setAnnualGoals(full.annualGoals ?? []);
      setAnnualBudgets(full.annualBudgets ?? []);
      if (full.indicatorDescription) setIndicatorDescription(full.indicatorDescription);
      if (full.indicatorType) setIndicatorType(full.indicatorType);
      if (full.thematicCategoryId) setThematicCategoryId(full.thematicCategoryId);
      if (full.measurementUnitId) setMeasurementUnitId(full.measurementUnitId);
    }).catch(() => {
      setAnnualGoals([]);
      setAnnualBudgets([]);
    }).finally(() => setLoadingExtras(false));
  }, [node.id, node.name, node.code, node.weight, node.sdgIndicators]);

  function getGoalForYear(year: number): number {
    return annualGoals.find((g) => g.year === year)?.goalValue ?? 0;
  }

  function setGoalForYear(year: number, value: number) {
    setAnnualGoals((prev) => {
      const exists = prev.find((g) => g.year === year);
      if (exists) return prev.map((g) => g.year === year ? { ...g, goalValue: value } : g);
      return [...prev, { year, goalValue: value }];
    });
  }

  function getBudgetForYear(year: number): number {
    return annualBudgets.find((b) => b.year === year)?.budgetAmount ?? 0;
  }

  function setBudgetForYear(year: number, value: number) {
    setAnnualBudgets((prev) => {
      const exists = prev.find((b) => b.year === year);
      if (exists) return prev.map((b) => b.year === year ? { ...b, budgetAmount: value } : b);
      return [...prev, { year, budgetAmount: value }];
    });
  }

  const goalTotal = pacYears.reduce((s, y) => s + getGoalForYear(y), 0);
  const budgetTotal = pacYears.reduce((s, y) => s + getBudgetForYear(y), 0);

  async function handleSave() {
    if (sdgIds.length === 0) return;
    setSaving(true);
    setError(null);
    try {
      await activityApi.update(node.id, {
        name: name.trim(),
        sdgIndicatorIds: sdgIds,
        code: code.trim() || undefined,
        weight: weight ? Number(weight) : undefined,
        measurementUnit: node.measurementUnit ?? undefined,
        indicatorDescription: indicatorDescription.trim() || undefined,
        indicatorType: indicatorType || undefined,
        thematicCategoryId: thematicCategoryId || undefined,
        measurementUnitId: measurementUnitId || undefined,
        annualGoals,
        annualBudgets,
      });
      onSaved();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo actualizar'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <Section title="Información general">
        <div className="detail-panel__row">
          <Field label="Código PAC">
            {editable ? (
              <Input value={code} onChange={(e) => setCode(e.target.value)} />
            ) : (
              <ReadOnly value={code || '—'} />
            )}
          </Field>
          <Field label="Ponderación (%)">
            {editable ? (
              <Input
                type="number"
                value={weight}
                onChange={(e) => setWeight(e.target.value)}
              />
            ) : (
              <ReadOnly value={weight ? `${weight}%` : '—'} />
            )}
          </Field>
        </div>
        <Field label="Nombre">
          {editable ? (
            <Input value={name} onChange={(e) => setName(e.target.value)} />
          ) : (
            <ReadOnly value={name} />
          )}
        </Field>
        <Field label="Unidad de medida">
          {editable ? (
            <Select value={measurementUnitId} onChange={(e) => setMeasurementUnitId(e.target.value)}>
              <option value="">— Seleccione —</option>
              {measurementUnits.map((u) => (
                <option key={u.id} value={u.id}>{u.name}</option>
              ))}
            </Select>
          ) : (
            <ReadOnly value={node.measurementUnit ?? '—'} />
          )}
        </Field>
        <Field label="Meta global">
          <ReadOnly value={goalTotal > 0 ? String(goalTotal) : (node.goal != null ? String(node.goal) : '—')} />
        </Field>
      </Section>

      <Section title="Indicador y clasificación">
        <Field label="Indicador (descripción)">
          {editable ? (
            <textarea
              className="atom-input"
              rows={2}
              value={indicatorDescription}
              onChange={(e) => setIndicatorDescription(e.target.value)}
              placeholder="Descripción del indicador"
            />
          ) : (
            <ReadOnly value={indicatorDescription || '—'} />
          )}
        </Field>
        <div className="detail-panel__row">
          <Field label="Tipo de indicador">
            {editable ? (
              <div className="detail-panel__radios">
                {([['IMG', 'IMG'], ['COMPLEMENTARIO', 'Complementario'], ['PROPIO', 'Propio']] as const).map(([val, label]) => (
                  <label key={val} className="detail-panel__radio-option">
                    <input
                      type="radio"
                      name="indicatorType"
                      value={val}
                      checked={indicatorType === val}
                      onChange={(e) => setIndicatorType(e.target.value)}
                    />
                    {label}
                  </label>
                ))}
              </div>
            ) : (
              <ReadOnly value={{ IMG: 'IMG', COMPLEMENTARIO: 'Complementario', PROPIO: 'Propio' }[indicatorType] || indicatorType || '—'} />
            )}
          </Field>
          <Field label="Categoría / Temática">
            {editable ? (
              <Select value={thematicCategoryId} onChange={(e) => setThematicCategoryId(e.target.value)}>
                <option value="">— Seleccione —</option>
                {thematicCategories.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </Select>
            ) : (
              <ReadOnly value={thematicCategories.find((c) => c.id === thematicCategoryId)?.name ?? '—'} />
            )}
          </Field>
        </div>
      </Section>

      <Section title="Metas anuales">
        {loadingExtras ? (
          <p className="detail-panel__empty">Cargando...</p>
        ) : pacYears.length === 0 ? (
          <p className="detail-panel__empty">Sin PAC activo</p>
        ) : (
          <table className="annual-table">
            <thead>
              <tr>
                {pacYears.map((y) => <th key={y}>{y}</th>)}
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                {pacYears.map((y) => (
                  <td key={y}>
                    {editable ? (
                      <input
                        type="number"
                        className="annual-table__input"
                        value={getGoalForYear(y)}
                        onChange={(e) => setGoalForYear(y, Number(e.target.value))}
                      />
                    ) : (
                      getGoalForYear(y)
                    )}
                  </td>
                ))}
                <td className="annual-table__total">{goalTotal}</td>
              </tr>
            </tbody>
          </table>
        )}
      </Section>

      <Section title="Recursos anuales">
        {loadingExtras ? (
          <p className="detail-panel__empty">Cargando...</p>
        ) : pacYears.length === 0 ? (
          <p className="detail-panel__empty">Sin PAC activo</p>
        ) : (
          <table className="annual-table">
            <thead>
              <tr>
                {pacYears.map((y) => <th key={y}>{y}</th>)}
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                {pacYears.map((y) => (
                  <td key={y}>
                    {editable ? (
                      <input
                        type="number"
                        className="annual-table__input"
                        value={getBudgetForYear(y)}
                        onChange={(e) => setBudgetForYear(y, Number(e.target.value))}
                      />
                    ) : (
                      new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(getBudgetForYear(y))
                    )}
                  </td>
                ))}
                <td className="annual-table__total">
                  {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(budgetTotal)}
                </td>
              </tr>
            </tbody>
          </table>
        )}
      </Section>

      <Section title="Indicadores ODS vinculados">
        {editable ? (
          <SdgCheckboxGroup
            options={sdgOptions}
            selectedIds={sdgIds}
            onChange={setSdgIds}
          />
        ) : (
          <ul className="detail-panel__list">
            {node.sdgIndicators.map((s) => (
              <li key={s.id}>ODS {s.code}</li>
            ))}
            {node.sdgIndicators.length === 0 && (
              <li className="detail-panel__empty">Sin ODS vinculados</li>
            )}
          </ul>
        )}
      </Section>

      {error && <div className="molecule-field-error">{error}</div>}
      {editable && (
        <Button onClick={handleSave} disabled={saving} style={{ marginTop: 16 }}>
          {saving ? 'Guardando...' : 'Guardar cambios'}
        </Button>
      )}
    </>
  );
}

function Section({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <div className="detail-panel__section">
      <div className="detail-panel__section-title">{title}</div>
      {children}
    </div>
  );
}

function Field({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="detail-panel__field">
      <label className="detail-panel__label">{label}</label>
      {children}
    </div>
  );
}

function ReadOnly({ value }: { value: string }) {
  return <div className="detail-panel__readonly">{value}</div>;
}
