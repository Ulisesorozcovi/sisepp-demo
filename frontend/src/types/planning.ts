export type InstrumentType = 'PGAR' | 'PAC' | 'POAI';
export type InstrumentStatus = 'ACTIVO' | 'CERRADO' | 'EN_FORMULACION';

export interface PlanningInstrument {
  id: string;
  type: InstrumentType;
  name: string;
  startYear: number;
  endYear: number;
  parentId: string | null;
  approvedDate: string | null;
  approvalRef: string | null;
  status: InstrumentStatus;
}

export interface CreatePlanningInstrumentRequest {
  type: InstrumentType;
  name: string;
  startYear: number;
  endYear: number;
  parentId?: string;
  approvedDate?: string;
  approvalRef?: string;
}

export interface UpdatePlanningInstrumentRequest {
  name: string;
  startYear: number;
  endYear: number;
  approvedDate?: string;
  approvalRef?: string;
  status?: InstrumentStatus;
}
