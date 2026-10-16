import type { ReactNode } from 'react';

interface Props {
  tone: 'director' | 'coordinador' | 'staff' | 'tecnico' | 'activo' | 'inactivo';
  children: ReactNode;
}

export function Badge({ tone, children }: Props) {
  return <span className={`atom-badge atom-badge--${tone}`}>{children}</span>;
}
