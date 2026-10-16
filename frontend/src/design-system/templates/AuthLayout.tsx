import type { ReactNode } from 'react';
import { Logo } from '../atoms/Logo';

export function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="login-layout">
      <div className="login-brand">
        <Logo />

        <div className="login-brand-copy">
          <p style={{ fontSize: 12, letterSpacing: '.06em', color: 'var(--muted)', fontWeight: 600, margin: 0 }}>
            DEMO DE PORTAFOLIO
          </p>
          <h1>Seguimiento de planes y proyectos de inversión pública.</h1>
          <p>
            Programa tareas, registra avances y consulta el cumplimiento del plan de acción 2024–2027 en un solo lugar,
            con trazabilidad completa por línea, programa y proyecto.
          </p>
        </div>
      </div>

      <div className="login-form-side">{children}</div>
    </div>
  );
}
