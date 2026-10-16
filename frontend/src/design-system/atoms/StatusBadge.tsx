import type { TaskStatus } from '../../types';

const CONFIG: Record<TaskStatus, { label: string; className: string }> = {
  PROGRAMADA: { label: 'Programada', className: 'task-status-pill--programada' },
  EN_EJECUCION: { label: 'En ejecución', className: 'task-status-pill--en_ejecucion' },
  RETRASADA: { label: 'Retrasada', className: 'task-status-pill--retrasada' },
  COMPLETADA: { label: 'Completada', className: 'task-status-pill--completada' },
  NO_APLICA: { label: 'No aplica', className: 'task-status-pill--no_aplica' },
  CANCELADA: { label: 'Cancelada', className: 'task-status-pill--cancelada' },
};

interface Props {
  status: TaskStatus;
}

export function StatusBadge({ status }: Props) {
  const { label, className } = CONFIG[status];
  return <span className={`task-status-pill ${className}`}>{label}</span>;
}
