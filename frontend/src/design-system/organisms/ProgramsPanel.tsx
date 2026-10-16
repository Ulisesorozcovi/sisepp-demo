import { useEffect, useState } from 'react';
import { programApi } from '../../api/structureApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { Program } from '../../types';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';

interface Props {
  strategicLineId: string | null;
  selectedId: string | null;
  onSelect: (program: Program) => void;
  refreshKey: number;
  onCreated: () => void;
}

export function ProgramsPanel({ strategicLineId, selectedId, onSelect, refreshKey, onCreated }: Props) {
  const [programs, setPrograms] = useState<Program[]>([]);
  const [search, setSearch] = useState('');
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [weight, setWeight] = useState('');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!strategicLineId) {
      setPrograms([]);
      return;
    }
    programApi.list(strategicLineId).then(setPrograms).catch(() => setPrograms([]));
  }, [strategicLineId, refreshKey]);

  const filtered = programs.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    (p.code ?? '').toLowerCase().includes(search.toLowerCase()),
  );

  function startEdit(program: Program) {
    setEditingId(program.id);
    setName(program.name);
    setCode(program.code ?? '');
    setWeight(program.weight != null ? String(program.weight) : '');
  }

  function cancelEdit() {
    setEditingId(null);
    setName('');
    setCode('');
    setWeight('');
    setError(null);
  }

  async function handleSubmit() {
    if (!name.trim()) return;
    setSaving(true);
    setError(null);
    try {
      if (editingId) {
        await programApi.update(editingId, {
          name: name.trim(),
          code: code.trim() || undefined,
          weight: weight ? Number(weight) : undefined,
        });
        setEditingId(null);
      } else if (strategicLineId) {
        await programApi.create({
          name: name.trim(),
          strategicLineId,
          code: code.trim() || undefined,
          weight: weight ? Number(weight) : undefined,
        });
      }
      setName('');
      setCode('');
      setWeight('');
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, editingId ? 'No se pudo actualizar' : 'No se pudo crear el Programa'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="structure-panel">
      <div className="structure-panel-header">
        <h3>Programas</h3>
        <p>{strategicLineId ? 'De la línea seleccionada' : 'Selecciona una línea estratégica'}</p>
      </div>
      <Input
        placeholder="Buscar programa..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ margin: '0 10px 6px', width: 'calc(100% - 20px)' }}
      />
      <ul className="structure-list">
        {strategicLineId && filtered.length === 0 && (
          <li className="empty-state">{programs.length === 0 ? 'Esta línea aún no tiene programas.' : 'Sin resultados.'}</li>
        )}
        {filtered.map((program) => (
          <li
            key={program.id}
            className={`structure-item ${selectedId === program.id ? 'selected' : ''}`}
            onClick={() => onSelect(program)}
          >
            <span className="title">{program.name}</span>
            {(program.code || program.weight != null) && (
              <span className="subtitle">
                {program.code && `Cód: ${program.code}`}
                {program.code && program.weight != null && ' · '}
                {program.weight != null && `Peso: ${program.weight}%`}
              </span>
            )}
            <button
              className="structure-item-edit"
              onClick={(e) => { e.stopPropagation(); startEdit(program); }}
              title="Editar"
            >
              ✎
            </button>
          </li>
        ))}
      </ul>
      {strategicLineId && (
        <div className="structure-panel-form">
          {error && <div className="molecule-field-error">{error}</div>}
          <Input placeholder={editingId ? 'Editar nombre...' : 'Nuevo programa'} value={name} onChange={(e) => setName(e.target.value)} />
          <div style={{ display: 'flex', gap: 6 }}>
            <Input placeholder="Código" value={code} onChange={(e) => setCode(e.target.value)} style={{ flex: 1 }} />
            <Input type="number" placeholder="Peso %" value={weight} onChange={(e) => setWeight(e.target.value)} style={{ width: 80 }} />
          </div>
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
