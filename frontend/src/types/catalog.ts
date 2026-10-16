import type { RecordStatus } from './common';

export interface MeasurementUnit {
  id: string;
  code: number;
  name: string;
  status: RecordStatus;
}

export interface PublicInvestmentProgram {
  id: string;
  code: string;
  name: string;
  sector: string;
  sourceUrl: string | null;
  status: RecordStatus;
}

export interface ThematicCategory {
  id: string;
  name: string;
  status: RecordStatus;
}

export type IndicatorType = 'GESTION' | 'RESULTADO' | 'PRODUCTO' | 'IMPACTO';

export interface ManagementIndicator {
  id: string;
  code: string;
  name: string;
  regulationRef: string | null;
  indicatorType: IndicatorType;
  status: RecordStatus;
}
