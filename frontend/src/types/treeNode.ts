import type { TreeLevel } from '../design-system/atoms/LevelIcon';
import type { RecordStatus } from './common';
import type { SdgIndicator } from './structure';

export interface TreeNodeBase {
  id: string;
  name: string;
  code?: string | null;
  weight?: number | null;
  status: RecordStatus;
}

export interface LineNode extends TreeNodeBase {
  type: 'linea';
  description: string | null;
}

export interface ProgramNodeDetail extends TreeNodeBase {
  type: 'programa';
  description: string | null;
  strategicLineId?: string;
}

export interface ProjectNodeDetail extends TreeNodeBase {
  type: 'proyecto';
  subdivision?: string | null;
  vigencia: number;
  coordinatorNames: string[];
  objective?: string | null;
  objectiveWeight?: number | null;
  pipId?: string | null;
}

export interface ActivityNodeDetail extends TreeNodeBase {
  type: 'actividad';
  measurementUnit: string | null;
  goal: number | null;
  sdgIndicators: SdgIndicator[];
  indicatorDescription?: string | null;
  indicatorType?: string | null;
  thematicCategoryId?: string | null;
  managementIndicatorId?: string | null;
  measurementUnitId?: string | null;
}

export type TreeNode = LineNode | ProgramNodeDetail | ProjectNodeDetail | ActivityNodeDetail;
