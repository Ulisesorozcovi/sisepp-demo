export interface PacInfo {
  name: string;
  startYear: number;
  endYear: number;
  approvalRef: string | null;
}

export interface ActivityReport {
  id: string;
  name: string;
  weight: number | null;
  indicator: string | null;
  indicatorType: string | null;
  category: string | null;
  measurementUnit: string | null;
  goals: Record<number, number>;
  budgets: Record<number, number>;
}

export interface ProjectReport {
  id: string;
  name: string;
  weight: number | null;
  pip: string | null;
  objective: string | null;
  objectiveWeight: number | null;
  activities: ActivityReport[];
  subtotalBudgets: Record<number, number>;
}

export interface ProgramReport {
  id: string;
  code: string | null;
  name: string;
  weight: number | null;
  projects: ProjectReport[];
}

export interface StrategicLineReport {
  id: string;
  name: string;
  weight: number | null;
  programs: ProgramReport[];
}

export interface PacMatrixResponse {
  pac: PacInfo;
  years: number[];
  strategicLines: StrategicLineReport[];
  grandTotal: Record<number, number>;
}

// --- Dashboard de avance físico (GET /reports/dashboard) ---

export interface DashboardTotals {
  lineas: number;
  programas: number;
  proyectos: number;
  actividades: number;
  tareas: number;
  completadas: number;
  enEjecucion: number;
  programadas: number;
  retrasadas: number;
  vencidas: number;
  porVencer: number;
  enRiesgo: number;
  completadasConRetraso: number;
}

export interface LineProgress {
  id: string;
  nombre: string;
  avance: number;
}

export interface ProgramProgress {
  id: string;
  codigo: string | null;
  nombre: string;
  linea: string;
  avance: number;
  peso: number | null;
}

export interface TaskAlert {
  id: string;
  tarea: string;
  actividad: string | null;
  proyectoCodigo: string | null;
  proyecto: string | null;
  proyectoId: string | null;
  responsable: string;
  meta: number | null;
  unidad: string | null;
  reportado: number | null;
  cumplimiento: number | null;
  fechaInicio: string | null;
  fechaVencimiento: string;
  /** Positivo: días vencida. Negativo: días que faltan para vencer. */
  dias: number;
  estado: string;
  observacion: string | null;
  /** Fracción del plazo ya consumida, de 0 a 1. */
  tiempoTranscurrido: number | null;
  /** Avance real sobre el esperado. Menor que 1 va por detrás. */
  ritmo: number | null;
  senal: TaskSignal;
  notaRiesgo: string | null;
}

/** Clasificación de cumplimiento calculada por el backend. */
export type TaskSignal = 'VENCIDA' | 'EN_RIESGO' | 'ATENCION' | 'EN_RUTA' | 'SIN_JUICIO';

/** Alcance de los datos según el rol de quien consulta. */
export type DashboardScope = 'GLOBAL' | 'PROYECTOS' | 'TAREAS';

export interface DashboardResponse {
  vigencia: number;
  rol: string;
  ambito: DashboardScope;
  avanceFisicoGlobal: number;
  totales: DashboardTotals;
  lineas: LineProgress[];
  programas: ProgramProgress[];
  tareasVencidas: TaskAlert[];
  tareasPorVencer: TaskAlert[];
  tareasEnRiesgo: TaskAlert[];
}
