import { useEffect, useState } from 'react';
import { projectApi } from '../../api/structureApi';
import { userApi } from '../../api/userApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { AppUser, ProjectItem } from '../../types';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Button } from '../atoms/Button';

interface Props {
  programId: string | null;
  selectedId: string | null;
  onSelect: (project: ProjectItem) => void;
  refreshKey: number;
  onCreated: () => void;
}

export function ProjectsPanel({ programId, selectedId, onSelect, refreshKey, onCreated }: Props) {
  const [projects, setProjects] = useState<ProjectItem[]>([]);
  const [coordinators, setCoordinators] = useState<AppUser[]>([]);
  const [search, setSearch] = useState('');
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [subdivision, setSubdivision] = useState('');
  const [weight, setWeight] = useState('');
  const [vigencia, setVigencia] = useState(new Date().getFullYear().toString());
  const [editingId, setEditingId] = useState<string | null>(null);
  const [coordinatorToAssign, setCoordinatorToAssign] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!programId) {
      setProjects([]);
      return;
    }
    projectApi.list({ programId }).then(setProjects).catch(() => setProjects([]));
  }, [programId, refreshKey]);

  useEffect(() => {
    userApi.list({ role: 'COORDINADOR', status: 'ACTIVO' }).then(setCoordinators).catch(() => setCoordinators([]));
  }, [refreshKey]);

  const filtered = projects.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    (p.code ?? '').toLowerCase().includes(search.toLowerCase()),
  );

  function startEdit(project: ProjectItem) {
    setEditingId(project.id);
    setName(project.name);
    setCode(project.code ?? '');
    setSubdivision(project.subdivision ?? '');
    setWeight(project.weight != null ? String(project.weight) : '');
    setVigencia(String(project.vigencia));
  }

  function cancelEdit() {
    setEditingId(null);
    setName('');
    setCode('');
    setSubdivision('');
    setWeight('');
    setVigencia(new Date().getFullYear().toString());
    setError(null);
  }

  async function handleSubmit() {
    if (!name.trim()) return;
    setSaving(true);
    setError(null);
    try {
      if (editingId) {
        await projectApi.update(editingId, {
          name: name.trim(),
          vigencia: Number(vigencia),
          code: code.trim() || undefined,
          subdivision: subdivision.trim() || undefined,
          weight: weight ? Number(weight) : undefined,
        });
        setEditingId(null);
      } else if (programId) {
        await projectApi.create({
          name: name.trim(),
          programId,
          vigencia: Number(vigencia),
          code: code.trim() || undefined,
          subdivision: subdivision.trim() || undefined,
          weight: weight ? Number(weight) : undefined,
        });
      }
      setName('');
      setCode('');
      setSubdivision('');
      setWeight('');
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, editingId ? 'No se pudo actualizar' : 'No se pudo crear el Proyecto'));
    } finally {
      setSaving(false);
    }
  }

  const selectedProject = projects.find((p) => p.id === selectedId) ?? null;
  const availableCoordinators = coordinators.filter(
    (c) => !selectedProject?.coordinatorIds.includes(c.id),
  );

  async function handleAssign() {
    if (!selectedProject || !coordinatorToAssign) return;
    try {
      await projectApi.assignCoordinator(selectedProject.id, { userId: coordinatorToAssign });
      setCoordinatorToAssign('');
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo asignar el Coordinador'));
    }
  }

  async function handleRemove(userId: string) {
    if (!selectedProject) return;
    try {
      await projectApi.removeCoordinator(selectedProject.id, userId);
      onCreated();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo quitar el Coordinador'));
    }
  }

  return (
    <div className="structure-panel">
      <div className="structure-panel-header">
        <h3>Proyectos</h3>
        <p>{programId ? 'Del programa seleccionado' : 'Selecciona un programa'}</p>
      </div>
      <Input
        placeholder="Buscar proyecto..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ margin: '0 10px 6px', width: 'calc(100% - 20px)' }}
      />
      <ul className="structure-list">
        {programId && filtered.length === 0 && (
          <li className="empty-state">{projects.length === 0 ? 'Este programa aún no tiene proyectos.' : 'Sin resultados.'}</li>
        )}
        {filtered.map((project) => (
          <li
            key={project.id}
            className={`structure-item ${selectedId === project.id ? 'selected' : ''}`}
            onClick={() => onSelect(project)}
          >
            <span className="title">{project.name}</span>
            <span className="subtitle">
              Vigencia {project.vigencia}
              {project.code && ` · Cód: ${project.code}`}
              {project.subdivision && ` · ${project.subdivision}`}
              {project.weight != null && ` · Peso: ${project.weight}%`}
            </span>
            <button
              className="structure-item-edit"
              onClick={(e) => { e.stopPropagation(); startEdit(project); }}
              title="Editar"
            >
              ✎
            </button>
          </li>
        ))}
      </ul>

      {selectedProject && (
        <div className="structure-panel-form">
          <div>
            {selectedProject.coordinatorIds.length === 0 && (
              <span className="subtitle" style={{ fontSize: 11, color: 'var(--ink-600)' }}>
                Sin coordinadores asignados
              </span>
            )}
            {selectedProject.coordinatorIds.map((coordId) => {
              const user = coordinators.find((c) => c.id === coordId);
              return (
                <span className="coordinator-chip" key={coordId}>
                  {user?.fullName ?? coordId}
                  <button onClick={() => handleRemove(coordId)}>×</button>
                </span>
              );
            })}
          </div>
          <Select value={coordinatorToAssign} onChange={(e) => setCoordinatorToAssign(e.target.value)}>
            <option value="">Asignar coordinador...</option>
            {availableCoordinators.map((c) => (
              <option key={c.id} value={c.id}>{c.fullName}</option>
            ))}
          </Select>
          <Button variant="ghost" onClick={handleAssign} disabled={!coordinatorToAssign}>
            Asignar
          </Button>
        </div>
      )}

      {programId && (
        <div className="structure-panel-form">
          {error && <div className="molecule-field-error">{error}</div>}
          <Input placeholder={editingId ? 'Editar nombre...' : 'Nuevo proyecto'} value={name} onChange={(e) => setName(e.target.value)} />
          <div style={{ display: 'flex', gap: 6 }}>
            <Input placeholder="Código" value={code} onChange={(e) => setCode(e.target.value)} style={{ flex: 1 }} />
            <Input type="number" placeholder="Vigencia" value={vigencia} onChange={(e) => setVigencia(e.target.value)} style={{ width: 90 }} />
          </div>
          <div style={{ display: 'flex', gap: 6 }}>
            <Input placeholder="Subdivisión" value={subdivision} onChange={(e) => setSubdivision(e.target.value)} style={{ flex: 1 }} />
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
