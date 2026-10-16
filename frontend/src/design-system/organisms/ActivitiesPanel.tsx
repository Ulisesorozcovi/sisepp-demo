import { useEffect, useState } from 'react';
import { activityApi, sdgIndicatorApi } from '../../api/structureApi';
import { catalogApi } from '../../api/catalogApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { ActivityItem, SdgIndicator, MeasurementUnit, ThematicCategory } from '../../types';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';
import { SdgCheckboxGroup } from '../molecules/SdgCheckboxGroup';

interface Props {
  projectId: string | null;
  selectedId: string | null;
  onSelect: (activity: ActivityItem) => void;
  refreshKey: number;
  onCreated: () => void;
}

export function ActivitiesPanel({ projectId, selectedId, onSelect, refreshKey, onCreated }: Props) {
  const [activities, setActivities] = useState<ActivityItem[]>([]);
  const [sdgOptions, setSdgOptions] = useState<SdgIndicator[]>([]);
  const [search, setSearch] = useState('');
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [measurementUnit, setMeasurementUnit] = useState('');
  const [goal, setGoal] = useState('');
  const [weight, setWeight] = useState('');
  const [indicatorDescription, setIndicatorDescription] = useState('');
  const [indicatorType, setIndicatorType] = useState('');
  const [thematicCategoryId, setThematicCategoryId] = useState('');
  const [measurementUnitId, setMeasurementUnitId] = useState('');
  const [measurementUnits, setMeasurementUnits] = useState<MeasurementUnit[]>([]);
  const [thematicCategories, setThematicCategories] = useState<ThematicCategory[]>([]);
  const [sdgIds, setSdgIds] = useState<number[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!projectId) {
      setActivities([]);
      return;
    }
    activityApi.list(projectId).then(setActivities).catch(() => setActivities([]));
  }, [projectId, refreshKey]);

  useEffect(() => {
    sdgIndicatorApi.list().then(setSdgOptions).catch(() => setSdgOptions([]));
    catalogApi.listMeasurementUnits().then(setMeasurementUnits).catch(() => setMeasurementUnits([]));
    catalogApi.listThematicCategories().then(setThematicCategories).catch(() => setThematicCategories([]));
  }, []);

  const filtered = activities.filter((a) =>
    a.name.toLowerCase().includes(search.toLowerCase()) ||
    (a.code ?? '').toLowerCase().includes(search.toLowerCase()),
  );

  function startEdit(activity: ActivityItem) {
    setEditingId(activity.id);
    setName(activity.name);
    setCode(activity.code ?? '');
    setMeasurementUnit(activity.measurementUnit ?? '');
    setGoal(activity.goal != null ? String(activity.goal) : '');
    setWeight(activity.weight != null ? String(activity.weight) : '');
    setIndicatorDescription(activity.indicatorDescription ?? '');
    setIndicatorType(activity.indicatorType ?? '');
    setThematicCategoryId(activity.thematicCategoryId ?? '');
    setMeasurementUnitId(activity.measurementUnitId ?? '');
    setSdgIds(activity.sdgIndicatorIds);
  }

  function cancelEdit() {
    setEditingId(null);
    setName('');
    setCode('');
    setMeasurementUnit('');
    setGoal('');
    setWeight('');
    setIndicatorDescription('');
    setIndicatorType('');
    setThematicCategoryId('');
    setMeasurementUnitId('');
    setSdgIds([]);
    setError(null);
  }

  async function handleSubmit() {
    if (!name.trim() || sdgIds.length === 0) return;
    setSaving(true);
    setError(null);
    try {
      const shared = {
        name: name.trim(),
        sdgIndicatorIds: sdgIds,
        code: code.trim() || undefined,
        measurementUnit: measurementUnit.trim() || undefined,
        goal: goal ? Number(goal) : undefined,
        weight: weight ? Number(weight) : undefined,
        indicatorDescription: indicatorDescription.trim() || undefined,
        indicatorType: indicatorType || undefined,
        thematicCategoryId: thematicCategoryId || undefined,
        measurementUnitId: measurementUnitId || undefined,
      };
      if (editingId) {
        await activityApi.update(editingId, shared);
        setEditingId(null);
      } else if (projectId) {
        await activityApi.create({ ...shared, projectId });
      }
      setName('');
      setCode('');
      setMeasurementUnit('');
      setGoal('');
      setWeight('');
      setIndicatorDescription('');
      setIndicatorType('');
      setThematicCategoryId('');
      setMeasurementUnitId('');
      setSdgIds([]);
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, editingId ? 'No se pudo actualizar' : 'No se pudo crear la Actividad'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="structure-panel">
      <div className="structure-panel-header">
        <h3>Actividades</h3>
        <p>{projectId ? 'Del proyecto seleccionado' : 'Selecciona un proyecto'}</p>
      </div>
      <Input
        placeholder="Buscar actividad..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ margin: '0 10px 6px', width: 'calc(100% - 20px)' }}
      />
      <ul className="structure-list">
        {projectId && filtered.length === 0 && (
          <li className="empty-state">{activities.length === 0 ? 'Este proyecto aún no tiene actividades.' : 'Sin resultados.'}</li>
        )}
        {filtered.map((activity) => (
          <li
            key={activity.id}
            className={`structure-item ${selectedId === activity.id ? 'selected' : ''}`}
            onClick={() => onSelect(activity)}
          >
            <span className="title">{activity.name}</span>
            <span className="subtitle">
              {activity.sdgIndicatorIds
                .map((id) => sdgOptions.find((s) => s.id === id)?.code ?? `ODS-${id}`)
                .join(', ')}
              {activity.code && ` · Cód: ${activity.code}`}
              {activity.measurementUnit && ` · ${activity.measurementUnit}`}
              {activity.goal != null && ` · Meta: ${activity.goal}`}
              {activity.weight != null && ` · Peso: ${activity.weight}%`}
            </span>
            <button
              className="structure-item-edit"
              onClick={(e) => { e.stopPropagation(); startEdit(activity); }}
              title="Editar"
            >
              ✎
            </button>
          </li>
        ))}
      </ul>
      {projectId && (
        <div className="structure-panel-form">
          {error && <div className="molecule-field-error">{error}</div>}
          <Input placeholder={editingId ? 'Editar nombre...' : 'Nueva actividad'} value={name} onChange={(e) => setName(e.target.value)} />
          <div style={{ display: 'flex', gap: 6 }}>
            <Input placeholder="Código" value={code} onChange={(e) => setCode(e.target.value)} style={{ flex: 1 }} />
            <div style={{ flex: 1 }}>
              <Select value={measurementUnitId} onChange={(e) => setMeasurementUnitId(e.target.value)}>
                <option value="">Unidad de medida</option>
                {measurementUnits.map((u) => (
                  <option key={u.id} value={u.id}>{u.name}</option>
                ))}
              </Select>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 6 }}>
            <Input type="number" placeholder="Meta" value={goal} onChange={(e) => setGoal(e.target.value)} style={{ flex: 1 }} />
            <Input type="number" placeholder="Peso %" value={weight} onChange={(e) => setWeight(e.target.value)} style={{ width: 80 }} />
          </div>
          <Input placeholder="Indicador (descripción)" value={indicatorDescription} onChange={(e) => setIndicatorDescription(e.target.value)} />
          <div style={{ display: 'flex', gap: 6 }}>
            <Select value={indicatorType} onChange={(e) => setIndicatorType(e.target.value)} style={{ flex: 1 }}>
              <option value="">Tipo indicador</option>
              <option value="IMG">IMG</option>
              <option value="COMPLEMENTARIO">Complementario</option>
              <option value="PROPIO">Propio</option>
            </Select>
            <Select value={thematicCategoryId} onChange={(e) => setThematicCategoryId(e.target.value)} style={{ flex: 1 }}>
              <option value="">Categoría temática</option>
              {thematicCategories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </Select>
          </div>
          <SdgCheckboxGroup options={sdgOptions} selectedIds={sdgIds} onChange={setSdgIds} />
          <div style={{ display: 'flex', gap: 6 }}>
            <Button variant="ghost" onClick={handleSubmit} disabled={saving} style={{ flex: 1 }}>
              {saving ? 'Guardando...' : editingId ? 'Actualizar' : '+ Agregar'}
            </Button>
            {editingId && (
              <Button variant="ghost" onClick={cancelEdit}>Cancelar</Button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
