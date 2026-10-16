import { useEffect, useState } from 'react';
import { strategicLineApi } from '../../api/structureApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { StrategicLine } from '../../types';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';

interface Props {
  selectedId: string | null;
  onSelect: (line: StrategicLine) => void;
  refreshKey: number;
  onCreated: () => void;
}

export function StrategicLinesPanel({ selectedId, onSelect, refreshKey, onCreated }: Props) {
  const [lines, setLines] = useState<StrategicLine[]>([]);
  const [search, setSearch] = useState('');
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    strategicLineApi.list().then(setLines).catch(() => setLines([]));
  }, [refreshKey]);

  const filtered = lines.filter((l) =>
    l.name.toLowerCase().includes(search.toLowerCase()),
  );

  function startEdit(line: StrategicLine) {
    setEditingId(line.id);
    setName(line.name);
  }

  function cancelEdit() {
    setEditingId(null);
    setName('');
    setError(null);
  }

  async function handleSubmit() {
    if (!name.trim()) return;
    setSaving(true);
    setError(null);
    try {
      if (editingId) {
        await strategicLineApi.update(editingId, { name: name.trim() });
        setEditingId(null);
      } else {
        await strategicLineApi.create({ name: name.trim() });
      }
      setName('');
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, editingId ? 'No se pudo actualizar' : 'No se pudo crear la Línea Estratégica'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="structure-panel">
      <div className="structure-panel-header">
        <h3>Líneas Estratégicas</h3>
        <p>Primer nivel del PAC</p>
      </div>
      <Input
        placeholder="Buscar línea..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ margin: '0 10px 6px', width: 'calc(100% - 20px)' }}
      />
      <ul className="structure-list">
        {filtered.length === 0 && <li className="empty-state">{lines.length === 0 ? 'Aún no hay líneas registradas.' : 'Sin resultados.'}</li>}
        {filtered.map((line) => (
          <li
            key={line.id}
            className={`structure-item ${selectedId === line.id ? 'selected' : ''}`}
            onClick={() => onSelect(line)}
          >
            <span className="title">{line.name}</span>
            <button
              className="structure-item-edit"
              onClick={(e) => { e.stopPropagation(); startEdit(line); }}
              title="Editar"
            >
              ✎
            </button>
          </li>
        ))}
      </ul>
      <div className="structure-panel-form">
        {error && <div className="molecule-field-error">{error}</div>}
        <Input
          placeholder={editingId ? 'Editar nombre...' : 'Nueva línea estratégica'}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <div style={{ display: 'flex', gap: 6 }}>
          <Button variant="ghost" onClick={handleSubmit} disabled={saving} style={{ flex: 1 }}>
            {saving ? 'Guardando...' : editingId ? 'Actualizar' : '+ Agregar'}
          </Button>
          {editingId && (
            <Button variant="ghost" onClick={cancelEdit}>Cancelar</Button>
          )}
        </div>
      </div>
    </div>
  );
}
