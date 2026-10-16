import { useState, useRef, useEffect, useCallback } from 'react';
import { Icon } from '../atoms/Icon';
import { Select } from '../atoms/Select';
import { LevelIcon, type TreeLevel } from '../atoms/LevelIcon';
import { createPortal } from 'react-dom';

interface Props {
  vigencia: number;
  onVigenciaChange: (v: number) => void;
  vigenciaOptions: number[];
  visibleLevels: Set<TreeLevel>;
  onToggleLevel: (l: TreeLevel) => void;
  onExpandAll: () => void;
  onCollapseAll: () => void;
  onAddItem: (level: TreeLevel) => void;
}

const LEVELS: { key: TreeLevel; label: string }[] = [
  { key: 'linea', label: 'Líneas' },
  { key: 'programa', label: 'Programas' },
  { key: 'proyecto', label: 'Proyectos' },
  { key: 'actividad', label: 'Actividades' },
];

export function TreeToolbar({
  vigencia,
  onVigenciaChange,
  vigenciaOptions,
  visibleLevels,
  onToggleLevel,
  onExpandAll,
  onCollapseAll,
  onAddItem,
}: Props) {
  const [menuOpen, setMenuOpen] = useState(false);
  const btnRef = useRef<HTMLButtonElement>(null);
  const menuRef = useRef<HTMLDivElement>(null);
  const [menuPos, setMenuPos] = useState({ top: 0, left: 0 });

  const openMenu = useCallback(() => {
    if (btnRef.current) {
      const r = btnRef.current.getBoundingClientRect();
      setMenuPos({ top: r.bottom + 4, left: r.left });
    }
    setMenuOpen(true);
  }, []);

  useEffect(() => {
    if (!menuOpen) return;
    function close(e: MouseEvent) {
      const target = e.target as Node;
      if (menuRef.current?.contains(target)) return;
      if (btnRef.current?.contains(target)) return;
      setMenuOpen(false);
    }
    document.addEventListener('mousedown', close);
    return () => document.removeEventListener('mousedown', close);
  }, [menuOpen]);

  return (
    <div className="pac-tree-toolbar">
      <Select
        value={vigencia}
        onChange={(e) => onVigenciaChange(Number(e.target.value))}
        style={{ maxWidth: 100, padding: '6px 10px', fontSize: 13 }}
      >
        {vigenciaOptions.map((y) => (
          <option key={y} value={y}>{y}</option>
        ))}
      </Select>

      <div className="pac-tree-levels">
        {LEVELS.map((l) => (
          <button
            key={l.key}
            className={`pac-tree-level-chip ${visibleLevels.has(l.key) ? 'active' : ''}`}
            onClick={() => onToggleLevel(l.key)}
          >
            <LevelIcon level={l.key} />
            {l.label}
          </button>
        ))}
      </div>

      <button
        ref={btnRef}
        className="pac-tree-toolbar-btn"
        onClick={() => menuOpen ? setMenuOpen(false) : openMenu()}
        title="Nuevo elemento"
      >
        <Icon name="plus" size={14} /> Nuevo
      </button>

      {menuOpen && createPortal(
        <div
          ref={menuRef}
          className="pac-tree-add-menu"
          style={{ position: 'fixed', top: menuPos.top, left: menuPos.left }}
        >
          {LEVELS.map((l) => (
            <button key={l.key} onClick={() => { onAddItem(l.key); setMenuOpen(false); }}>
              <LevelIcon level={l.key} />
              Nueva {l.key === 'linea' ? 'Línea Estratégica' : l.key === 'programa' ? 'Programa' : l.key === 'proyecto' ? 'Proyecto' : 'Actividad'}
            </button>
          ))}
        </div>,
        document.body,
      )}

      <div className="spacer" />

      <div className="pac-tree-toolbar-actions">
        <button className="pac-tree-toolbar-btn" onClick={onExpandAll} title="Expandir todo">
          <Icon name="expand-all" size={16} />
        </button>
        <button className="pac-tree-toolbar-btn" onClick={onCollapseAll} title="Contraer todo">
          <Icon name="collapse-all" size={16} />
        </button>
      </div>
    </div>
  );
}
