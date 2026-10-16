import type { ReactNode } from 'react';

interface Props {
  children: ReactNode;
}

export function Alert({ children }: Props) {
  return <div className="molecule-alert">{children}</div>;
}
