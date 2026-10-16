import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { useAuth } from '../context/AuthContext';
import { Icon, type IconName } from '../design-system/atoms/Icon';
import { reportApi } from '../api/reportApi';
import { getApiErrorMessage } from '../utils/apiError';
import { TaskAlertModal } from '../design-system/organisms/TaskAlertModal';
import type { UserRole, DashboardResponse, DashboardScope, DashboardTotals, TaskAlert } from '../types';

interface ActionCard {
  title: string;
  description: string;
  icon: IconName;
  color: string;
  to: string;
  roles: UserRole[];
}

const ACTION_CARDS: ActionCard[] = [
  {
    title: 'Instrumentos de Planificación',
    description: 'Registre y gestione PGAR, PAC y POAI — la base de la estructura de planificación.',
    icon: 'planning',
    color: 'var(--ink-900)',
    to: '/instrumentos',
    roles: ['DIRECTOR', 'STAFF'],
  },
  {
    title: 'Estructura del PAC',
    description: 'Cree Líneas Estratégicas, Programas, Proyectos y Actividades del PAC.',
    icon: 'structure',
    color: 'var(--green-700)',
    to: '/estructura',
    roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'],
  },
  {
    title: 'Catálogo de Tareas',
    description: 'Registre las tareas técnicas que componen cada actividad del PAC.',
    icon: 'task-catalog',
    color: 'var(--ink-600)',
    to: '/catalogo-tareas',
    roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'],
  },
  {
    title: 'Cronograma de Tareas',
    description: 'Consulte plazos cronológicos, responsables técnicos y metas físicas de cada tarea.',
    icon: 'schedule',
    color: 'var(--blue-600)',
    to: '/cronograma',
    roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'],
  },
  {
    title: 'Registro de Avance',
    description: 'Reporte la ejecución física del mes de cada tarea, estados y enlaces de evidencia.',
    icon: 'progress',
    color: 'var(--blue-700)',
    to: '/seguimiento',
    roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'],
  },
  {
    title: 'Registrar Avance',
    description: 'Formulario para registrar el avance físico mensual de cada tarea.',
    icon: 'edit',
    color: 'var(--green-600)',
    to: '/registrar-avance',
    roles: ['COORDINADOR', 'DIRECTOR', 'STAFF', 'TECNICO'],
  },
  {
    title: 'Mis Tareas',
    description: 'Consulte las tareas asignadas y registre el avance de cada una.',
    icon: 'my-tasks',
    color: 'var(--ink-600)',
    to: '/mis-tareas',
    roles: ['TECNICO'],
  },
  {
    title: 'Consulta de Metas',
    description: 'Balance comparativo de metas programadas vs. avance ejecutado por tarea.',
    icon: 'target',
    color: 'var(--blue-600)',
    to: '/consulta-metas',
    roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'],
  },
  {
    title: 'Ejecución Financiera',
    description: 'Balance de avance físico y ejecución presupuestal de los proyectos.',
    icon: 'financial',
    color: 'var(--green-600)',
    to: '/financiero',
    roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'],
  },
  {
    title: 'Reportes',
    description: 'Genera y descarga la matriz del PAC con estructura, metas y presupuesto.',
    icon: 'report',
    color: 'var(--blue-700)',
    to: '/reportes',
    roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'],
  },
  {
    title: 'Usuarios',
    description: 'Gestione los usuarios del sistema, roles y estados de acceso.',
    icon: 'users',
    color: 'var(--ink-900)',
    to: '/usuarios',
    roles: ['DIRECTOR', 'STAFF'],
  },
];

/** Todos ven un resumen; el backend recorta los datos al alcance de cada rol. */
const DASHBOARD_ROLES: UserRole[] = ['DIRECTOR', 'STAFF', 'COORDINADOR', 'TECNICO'];

/**
 * Textos del resumen según el alcance que devuelve el backend. Un coordinador
 * no ve el PAC completo y un técnico solo ve sus tareas: el encabezado tiene
 * que decirlo, o los números se leen como si fueran de toda la entidad.
 */
const COPY_AMBITO: Record<DashboardScope, { titulo: string; pie: (t: DashboardTotals) => string }> = {
  GLOBAL: {
    titulo: 'Avance físico de la vigencia',
    pie: (t) =>
      `Consolidado ponderado de ${t.lineas} ${t.lineas === 1 ? 'línea' : 'líneas'}, ` +
      `${t.programas} ${t.programas === 1 ? 'programa' : 'programas'} y ` +
      `${t.proyectos} ${t.proyectos === 1 ? 'proyecto' : 'proyectos'}`,
  },
  PROYECTOS: {
    titulo: 'Avance de mis proyectos',
    pie: (t) =>
      `Consolidado ponderado de ${t.proyectos} ${t.proyectos === 1 ? 'proyecto asignado' : 'proyectos asignados'}, ` +
      `con ${t.actividades} ${t.actividades === 1 ? 'actividad' : 'actividades'}`,
  },
  TAREAS: {
    titulo: 'Avance de mis tareas',
    pie: (t) =>
      `Promedio de cumplimiento de ${t.tareas} ${t.tareas === 1 ? 'tarea asignada' : 'tareas asignadas'}`,
  },
};

/** Coincide con el @PreAuthorize de PATCH /tasks/{id}/reschedule. */
const RESCHEDULE_ROLES: UserRole[] = ['DIRECTOR', 'STAFF', 'COORDINADOR'];

function progressColor(pct: number): string {
  if (pct >= 70) return 'var(--green-600)';
  if (pct >= 40) return 'var(--yellow-600)';
  if (pct > 0) return 'var(--orange-600)';
  return 'var(--ink-400)';
}

function formatDays(dias: number): string {
  if (dias === 1) return '1 día';
  return `${dias} días`;
}

export function HomePage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const showDashboard = !!user && DASHBOARD_ROLES.includes(user.role);

  const [data, setData] = useState<DashboardResponse | null>(null);
  const [loading, setLoading] = useState(showDashboard);
  const [error, setError] = useState<string | null>(null);

  /**
   * Líneas con sus programas desplegados. Arrancan contraídas: con las 7 líneas
   * y 9 programas del PAC real, una lista plana es ilegible.
   */
  const [abiertas, setAbiertas] = useState<Set<string>>(new Set());

  /** Pestaña activa del panel de alertas y tarea abierta en el modal. */
  const [pestana, setPestana] = useState<'vencidas' | 'enRiesgo' | 'porVencer'>('vencidas');
  const [verTodas, setVerTodas] = useState(false);
  const [tareaSel, setTareaSel] = useState<TaskAlert | null>(null);

  function toggleLinea(id: string) {
    setAbiertas((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  }

  function toggleTodas() {
    setAbiertas((prev) => {
      if (!data) return prev;
      return prev.size === data.lineas.length ? new Set() : new Set(data.lineas.map((l) => l.id));
    });
  }

  const [recarga, setRecarga] = useState(0);

  useEffect(() => {
    if (!showDashboard) {
      setLoading(false);
      return;
    }
    let activo = true;
    reportApi
      .getDashboard()
      .then((d) => {
        if (activo) {
          setData(d);
          setLoading(false);
        }
      })
      .catch((e) => {
        if (activo) {
          setError(getApiErrorMessage(e, 'No fue posible cargar el resumen de avance.'));
          setLoading(false);
        }
      });
    return () => {
      activo = false;
    };
  }, [showDashboard, recarga]);

  const visibleCards = ACTION_CARDS.filter((card) => user && card.roles.includes(user.role));

  const listaActiva: TaskAlert[] =
    pestana === 'vencidas'
      ? (data?.tareasVencidas ?? [])
      : pestana === 'enRiesgo'
        ? (data?.tareasEnRiesgo ?? [])
        : (data?.tareasPorVencer ?? []);

  /** El backend revalida el permiso; además restringe al coordinador a sus proyectos. */
  const puedeReprogramar = !!user && RESCHEDULE_ROLES.includes(user.role);

  function abrirPanel(tab: 'vencidas' | 'enRiesgo' | 'porVencer') {
    setPestana(tab);
    setVerTodas(false);
    document.getElementById('panel-alertas')?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  const hayDatos = !!data && data.totales.tareas > 0;

  return (
    <DashboardLayout>
      <div className="home-hero">
        <h1>Bienvenido{user ? `, ${user.fullName.split(' ')[0]}` : ''}</h1>
        <p className="home-subtitle">
          Consola unificada para el seguimiento analítico-operativo de proyectos de inversión.
        </p>
      </div>

      {showDashboard && (
        <section className="dash">
          {loading && <div className="dash-placeholder">Cargando el resumen de la vigencia…</div>}

          {error && !loading && <div className="dash-placeholder dash-placeholder--error">{error}</div>}

          {!loading && !error && !hayDatos && (
            <div className="dash-placeholder">
              {data?.ambito === 'TAREAS'
                ? `No tiene tareas asignadas en la vigencia ${data?.vigencia}. Su coordinador es quien las programa.`
                : data?.ambito === 'PROYECTOS'
                  ? `No tiene proyectos asignados en la vigencia ${data?.vigencia}. Solicite la asignación a la Dirección.`
                  : `Todavía no hay tareas registradas para la vigencia ${data?.vigencia}. El resumen aparecerá cuando se programe la primera tarea.`}
            </div>
          )}

          {!loading && !error && hayDatos && data && (
            <>
              {/* --- Indicadores principales --- */}
              <div className="dash-kpis">
                <div className="dash-kpi dash-kpi--main">
                  <span className="dash-kpi-label">
                    {COPY_AMBITO[data.ambito].titulo} {data.vigencia}
                  </span>
                  <span className="dash-kpi-value">{data.avanceFisicoGlobal}%</span>
                  <div className="dash-meter" aria-hidden="true">
                    <div
                      className="dash-meter-fill"
                      style={{
                        width: `${Math.min(data.avanceFisicoGlobal, 100)}%`,
                        background: progressColor(data.avanceFisicoGlobal),
                      }}
                    />
                  </div>
                  <span className="dash-kpi-foot">{COPY_AMBITO[data.ambito].pie(data.totales)}</span>
                </div>

                <button
                  type="button"
                  className={`dash-kpi dash-kpi--action${data.totales.vencidas > 0 ? ' dash-kpi--alert' : ''}`}
                  onClick={() => abrirPanel('vencidas')}
                >
                  <span className="dash-kpi-label">
                    {data.ambito === 'GLOBAL' ? 'Tareas vencidas' : 'Mis tareas vencidas'}
                  </span>
                  <span className="dash-kpi-value">{data.totales.vencidas}</span>
                  <span className="dash-kpi-foot">
                    {data.totales.vencidas > 0 ? 'Requieren reprogramación' : 'Sin vencimientos pendientes'}
                  </span>
                </button>

                <button
                  type="button"
                  className={`dash-kpi dash-kpi--action${data.totales.enRiesgo > 0 ? ' dash-kpi--risk' : ''}`}
                  onClick={() => abrirPanel('enRiesgo')}
                >
                  <span className="dash-kpi-label">En riesgo</span>
                  <span className="dash-kpi-value">{data.totales.enRiesgo}</span>
                  <span className="dash-kpi-foot">
                    {data.totales.enRiesgo > 0
                      ? 'Avanzan por debajo de lo esperado'
                      : 'Todas avanzan al ritmo previsto'}
                  </span>
                </button>

                <button
                  type="button"
                  className="dash-kpi dash-kpi--action"
                  onClick={() => abrirPanel('porVencer')}
                >
                  <span className="dash-kpi-label">Vencen en 30 días</span>
                  <span className="dash-kpi-value">{data.totales.porVencer}</span>
                  <span className="dash-kpi-foot">Próximas a su fecha límite</span>
                </button>

                <div className="dash-kpi">
                  <span className="dash-kpi-label">
                    {data.ambito === 'GLOBAL' ? 'Tareas del PAC' : 'Mis tareas'}
                  </span>
                  <span className="dash-kpi-value">{data.totales.tareas}</span>
                  <div className="dash-chips">
                    <span className="dash-chip dash-chip--ok">{data.totales.completadas} completadas</span>
                    <span className="dash-chip dash-chip--run">{data.totales.enEjecucion} en ejecución</span>
                    <span className="dash-chip">{data.totales.programadas} programadas</span>
                    {data.totales.retrasadas > 0 && (
                      <span className="dash-chip dash-chip--late">{data.totales.retrasadas} retrasadas</span>
                    )}
                  </div>
                </div>
              </div>

              <div className={data.ambito === 'TAREAS' ? 'dash-cols dash-cols--single' : 'dash-cols'}>
                {/* --- Avance por línea, con sus programas desplegables --- */}
                {data.ambito !== 'TAREAS' && (
                <div className="dash-panel">
                  <div className="dash-panel-head">
                    <h2 className="dash-panel-title">Avance por línea estratégica</h2>
                    {data.lineas.length > 1 && (
                      <button type="button" className="dash-link dash-link--inline" onClick={toggleTodas}>
                        {abiertas.size === data.lineas.length ? 'Contraer todo' : 'Expandir todo'}
                      </button>
                    )}
                  </div>

                  <ul className="dash-tree">
                    {data.lineas.map((l) => {
                      const programas = data.programas.filter((p) => p.linea === l.nombre);
                      const abierta = abiertas.has(l.id);
                      return (
                        <li key={l.id} className="dash-tree-item">
                          <button
                            type="button"
                            className="dash-tree-head"
                            aria-expanded={abierta}
                            onClick={() => toggleLinea(l.id)}
                          >
                            <span className={`dash-caret${abierta ? ' dash-caret--open' : ''}`} aria-hidden="true">
                              ▸
                            </span>
                            <span className="dash-bar-name" title={l.nombre}>
                              {l.nombre}
                            </span>
                            <div className="dash-bar-track">
                              <div
                                className="dash-bar-fill"
                                style={{
                                  width: `${Math.min(l.avance, 100)}%`,
                                  background: progressColor(l.avance),
                                }}
                              />
                            </div>
                            <span className="dash-bar-pct">{l.avance}%</span>
                          </button>

                          {!abierta && programas.length > 0 && (
                            <span className="dash-tree-hint">
                              {programas.length} {programas.length === 1 ? 'programa' : 'programas'}
                            </span>
                          )}

                          {abierta && (
                            <ul className="dash-bars dash-bars--nested">
                              {programas.length === 0 && (
                                <li className="dash-empty dash-empty--nested">
                                  Esta línea todavía no tiene programas registrados.
                                </li>
                              )}
                              {programas.map((p) => (
                                <li key={p.id} className="dash-bar-row">
                                  <span className="dash-bar-name" title={p.nombre}>
                                    {p.codigo ? `${p.codigo} · ` : ''}
                                    {p.nombre}
                                  </span>
                                  <div className="dash-bar-track">
                                    <div
                                      className="dash-bar-fill"
                                      style={{
                                        width: `${Math.min(p.avance, 100)}%`,
                                        background: progressColor(p.avance),
                                      }}
                                    />
                                  </div>
                                  <span className="dash-bar-pct">{p.avance}%</span>
                                </li>
                              ))}
                            </ul>
                          )}
                        </li>
                      );
                    })}
                  </ul>
                </div>
                )}

                {/* --- Tareas que requieren atención --- */}
                <div className="dash-panel" id="panel-alertas">
                  <div className="dash-tabs" role="tablist">
                    <button
                      type="button"
                      role="tab"
                      aria-selected={pestana === 'vencidas'}
                      className={`dash-tab${pestana === 'vencidas' ? ' dash-tab--on' : ''}`}
                      onClick={() => setPestana('vencidas')}
                    >
                      Vencidas
                      {data.tareasVencidas.length > 0 && (
                        <span className="dash-count">{data.tareasVencidas.length}</span>
                      )}
                    </button>
                    <button
                      type="button"
                      role="tab"
                      aria-selected={pestana === 'enRiesgo'}
                      className={`dash-tab${pestana === 'enRiesgo' ? ' dash-tab--on' : ''}`}
                      onClick={() => setPestana('enRiesgo')}
                    >
                      En riesgo
                      {data.tareasEnRiesgo.length > 0 && (
                        <span className="dash-count dash-count--risk">{data.tareasEnRiesgo.length}</span>
                      )}
                    </button>
                    <button
                      type="button"
                      role="tab"
                      aria-selected={pestana === 'porVencer'}
                      className={`dash-tab${pestana === 'porVencer' ? ' dash-tab--on' : ''}`}
                      onClick={() => setPestana('porVencer')}
                    >
                      Vencen pronto
                      {data.tareasPorVencer.length > 0 && (
                        <span className="dash-count dash-count--soft">{data.tareasPorVencer.length}</span>
                      )}
                    </button>
                  </div>

                  {listaActiva.length === 0 ? (
                    <p className="dash-empty">
                      {pestana === 'enRiesgo'
                        ? 'Ninguna tarea va por debajo del ritmo esperado.'
                        : pestana === 'vencidas'
                        ? data.ambito === 'TAREAS'
                          ? 'Ninguna de sus tareas superó su fecha de vencimiento.'
                          : 'Ninguna tarea superó su fecha de vencimiento. Todo el cronograma está al día.'
                        : data.ambito === 'TAREAS'
                          ? 'Ninguna de sus tareas vence en los próximos 30 días.'
                          : 'Ninguna tarea vence en los próximos 30 días.'}
                    </p>
                  ) : (
                    <>
                      <ul className="dash-overdue">
                        {listaActiva.slice(0, verTodas ? undefined : 6).map((t) => (
                          <li key={t.id}>
                            <button
                              type="button"
                              className={`dash-alert dash-alert--${pestana === 'vencidas' ? 'late' : pestana === 'enRiesgo' ? 'risk' : 'soon'}`}
                              onClick={() => setTareaSel(t)}
                            >
                              <span className="dash-overdue-head">
                                <span className="dash-overdue-name">{t.tarea}</span>
                                <span
                                  className={`dash-overdue-days${
                                    pestana === 'vencidas' ? '' : pestana === 'enRiesgo' ? ' dash-overdue-days--risk' : ' dash-overdue-days--soft'
                                  }`}
                                >
                                  {pestana === 'vencidas'
                                    ? formatDays(t.dias)
                                    : pestana === 'enRiesgo' && t.ritmo !== null
                                      ? `${Math.round(t.ritmo * 100)}% del ritmo`
                                      : `en ${formatDays(Math.abs(t.dias))}`}
                                </span>
                              </span>
                              <span className="dash-overdue-meta">
                                {t.proyectoCodigo ? `${t.proyectoCodigo} · ` : ''}
                                {t.responsable} ·{' '}
                                {pestana === 'vencidas' ? 'venció' : 'vence'} el {t.fechaVencimiento}
                                {pestana === 'enRiesgo' && t.tiempoTranscurrido !== null && (
                                  <> · {Math.round(t.tiempoTranscurrido * 100)}% del plazo consumido</>
                                )}
                              </span>
                            </button>
                          </li>
                        ))}
                      </ul>

                      {listaActiva.length > 6 && (
                        <button type="button" className="dash-link" onClick={() => setVerTodas((v) => !v)}>
                          {verTodas
                            ? 'Ver menos'
                            : `Ver las ${listaActiva.length} tareas`}
                        </button>
                      )}

                      <p className="dash-hint">
                        {puedeReprogramar
                          ? 'Haga clic en una tarea para ver el detalle y reprogramarla.'
                          : 'Haga clic en una tarea para ver su detalle.'}
                      </p>
                    </>
                  )}
                </div>
              </div>
            </>
          )}
        </section>
      )}

      <h2 className="home-section-title">Módulos</h2>

      <TaskAlertModal
        task={tareaSel}
        puedeReprogramar={puedeReprogramar}
        onClose={() => setTareaSel(null)}
        onRescheduled={() => setRecarga((n) => n + 1)}
      />

      <div className="home-grid">
        {visibleCards.map((card, i) => (
          <button key={card.to} className="home-card" onClick={() => navigate(card.to)}>
            <div className="home-card-icon" style={{ background: card.color }}>
              <Icon name={card.icon} size={24} />
            </div>
            <h3 className="home-card-title">
              <span className="home-card-number">{i + 1}.</span> {card.title}
            </h3>
            <p className="home-card-desc">{card.description}</p>
          </button>
        ))}
      </div>
    </DashboardLayout>
  );
}
