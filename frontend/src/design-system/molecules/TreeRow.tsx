import { Icon } from '../atoms/Icon';
import { LevelIcon, type TreeLevel } from '../atoms/LevelIcon';
import type { ReactNode } from 'react';

const TYPE_LABELS: Record<TreeLevel, string> = {
  linea: 'Línea',
  programa: 'Programa',
  proyecto: 'Proyecto',
  actividad: 'Actividad',
};

interface Props {
  order: number;
  level: TreeLevel;
  depth: number;
  name: string;
  code?: string | null;
  status?: string;
  extra?: ReactNode;
  hasChildren: boolean;
  expanded: boolean;
  onToggle: () => void;
  onAdd?: () => void;
  onClick?: () => void;
}

export function TreeRow({
  order,
  level,
  depth,
  name,
  code,
  status,
  extra,
  hasChildren,
  expanded,
  onToggle,
  onAdd,
  onClick,
}: Props) {
  const indent = depth * 24;
  const stateClass = status === 'ACTIVO' ? 'active' : status === 'INACTIVO' ? 'closed' : '';

  return (
    <tr className={onClick ? 'tree-row--clickable' : ''} onClick={onClick}>
      <td className="tree-cell-order">{order}</td>
      <td className="tree-cell-type">
        <span className="tree-type-badge">
          <LevelIcon level={level} />
          {TYPE_LABELS[level]}
        </span>
      </td>
      <td>
        <div className="tree-cell-name" style={{ paddingLeft: indent }}>
          {hasChildren ? (
            <button className="tree-toggle" onClick={(e) => { e.stopPropagation(); onToggle(); }}>
              <Icon name={expanded ? 'chevron-down' : 'chevron-right'} size={14} />
            </button>
          ) : (
            <span className="tree-toggle--placeholder" />
          )}
          <span className={depth === 0 ? 'tree-name tree-name--bold' : 'tree-name'}>
            {name}
          </span>
          {code && <span className="tree-code">[{code}]</span>}
          {onAdd && (
            <button
              className="tree-row-add-btn"
              onClick={(e) => { e.stopPropagation(); onAdd(); }}
              title="Agregar hijo"
            >
              <Icon name="plus" size={14} />
            </button>
          )}
        </div>
      </td>
      <td className="tree-cell-state">
        <span className={`tree-state-dot ${stateClass}`}>
          {status === 'ACTIVO' ? 'Activo' : status === 'INACTIVO' ? 'Inactivo' : 'Nuevo'}
        </span>
      </td>
      <td className="tree-cell-detail">{extra}</td>
    </tr>
  );
}
