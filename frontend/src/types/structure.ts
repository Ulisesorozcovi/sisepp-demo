import type { RecordStatus } from './common';

export interface StrategicLine {
  id: string;
  name: string;
  description: string | null;
  status: RecordStatus;
}

export interface Program {
  id: string;
  name: string;
  code: string | null;
  weight: number | null;
  description: string | null;
  strategicLineId: string;
  status: RecordStatus;
}

export interface Project {
  id: string;
  name: string;
  code: string | null;
  subdivision: string | null;
  weight: number | null;
  objective: string | null;
  objectiveWeight: number | null;
  pipId: string | null;
  programId: string;
  vigencia: number;
  status: RecordStatus;
  coordinatorIds: string[];
}

export interface SdgIndicator {
  id: number;
  code: string;
  name: string;
  description: string | null;
}

export interface AnnualGoalEntry {
  year: number;
  goalValue: number;
}

export interface AnnualBudgetEntry {
  year: number;
  budgetAmount: number;
}

export interface Activity {
  id: string;
  name: string;
  code: string | null;
  measurementUnit: string | null;
  goal: number | null;
  weight: number | null;
  indicatorDescription: string | null;
  indicatorType: string | null;
  thematicCategoryId: string | null;
  managementIndicatorId: string | null;
  measurementUnitId: string | null;
  projectId: string;
  status: RecordStatus;
  sdgIndicatorIds: number[];
  annualGoals?: AnnualGoalEntry[];
  annualBudgets?: AnnualBudgetEntry[];
}

// --- Structure tree (GET /structure/tree) ---

export interface ActivityNode {
  id: string;
  name: string;
  code: string | null;
  measurementUnit: string | null;
  goal: number | null;
  weight: number | null;
  status: RecordStatus;
  sdgIndicators: SdgIndicator[];
}

export interface ProjectNode {
  id: string;
  name: string;
  code: string | null;
  subdivision: string | null;
  weight: number | null;
  vigencia: number;
  status: RecordStatus;
  coordinatorNames: string[];
  activities: ActivityNode[];
}

export interface ProgramNode {
  id: string;
  name: string;
  code: string | null;
  weight: number | null;
  description: string | null;
  status: RecordStatus;
  projects: ProjectNode[];
}

export interface StrategicLineNode {
  id: string;
  name: string;
  description: string | null;
  status: RecordStatus;
  programs: ProgramNode[];
}
