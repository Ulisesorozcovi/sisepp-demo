import { axiosClient } from './axiosClient';

export interface BulkAnnualValue {
  year: number;
  value: number;
}

export interface BulkActivity {
  name: string;
  code?: string;
  measurementUnit?: string;
  goal?: number;
  weight?: number;
  indicatorDescription?: string;
  indicatorType?: string;
  annualGoals?: BulkAnnualValue[];
}

export interface BulkProject {
  name: string;
  code?: string;
  subdivision?: string;
  weight?: number;
  objective?: string;
  objectiveWeight?: number;
  vigencia: number;
  activities?: BulkActivity[];
}

export interface BulkProgram {
  name: string;
  code?: string;
  weight?: number;
  description?: string;
  projects?: BulkProject[];
}

export interface BulkLine {
  name: string;
  description?: string;
  programs?: BulkProgram[];
}

export interface BulkPacRequest {
  pacId: string;
  lines: BulkLine[];
}

export const bulkApi = {
  loadPacStructure: (data: BulkPacRequest) =>
    axiosClient.post('/bulk/pac-structure', data).then((r) => r.data),
};
