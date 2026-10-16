import type { ReactNode } from 'react';
import { Logo } from '../atoms/Logo';
import { Icon } from '../atoms/Icon';

const FEATURES = [
  {
    icon: 'structure' as const,
    title: 'Planificación estratégica',
    desc: 'Jerarquía PGAR, PAC y POAI con líneas, programas y proyectos en un árbol interactivo.',
  },
  {
    icon: 'schedule' as const,
    title: 'Cronogramas y tareas',
    desc: 'Responsables, plazos y avance en vista Gantt con reprogramación trazable.',
  },
  {
    icon: 'chart' as const,
    title: 'Dashboard en tiempo real',
    desc: 'KPIs de avance físico, alertas de vencimiento y ejecución financiera consolidada.',
  },
  {
    icon: 'users' as const,
    title: '4 roles diferenciados',
    desc: 'Director, coordinador, técnico y staff — cada uno ve exactamente lo que necesita.',
  },
];

export function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="login-layout">
      <div className="login-brand">
        <div className="login-brand-top">
          <Logo />
        </div>

        <div className="login-brand-copy">
          <p className="login-eyebrow">DEMO DE PORTAFOLIO</p>
          <h1>Seguimiento de planes y proyectos de inversión pública.</h1>
          <p className="login-subtitle">
            Planifique, ejecute y evalúe proyectos de inversión con trazabilidad completa — desde la estrategia hasta la ejecución financiera.
          </p>
        </div>

        <div className="login-features">
          {FEATURES.map((f) => (
            <div key={f.icon} className="login-feature">
              <div className="login-feature-icon">
                <Icon name={f.icon} size={20} />
              </div>
              <div>
                <strong className="login-feature-title">{f.title}</strong>
                <p className="login-feature-desc">{f.desc}</p>
              </div>
            </div>
          ))}
        </div>

        <p className="login-brand-footer">
          React · TypeScript · Spring Boot · PostgreSQL
        </p>
      </div>

      <div className="login-form-side">{children}</div>
    </div>
  );
}
