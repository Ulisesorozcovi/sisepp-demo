export type { RecordStatus, ApiError } from './common';
export type { UserRole, UserStatus, AuthUser, LoginResponse } from './auth';
export type { UserResponse, CreateUserRequest, UpdateUserRequest } from './user';
export type {
  StrategicLine,
  Program,
  Project,
  SdgIndicator,
  Activity,
  AnnualGoalEntry,
  AnnualBudgetEntry,
  ActivityNode,
  ProjectNode,
  ProgramNode,
  StrategicLineNode,
} from './structure';
export type {
  TaskStatus,
  Task,
  CreateTaskRequest,
  RescheduleTaskRequest,
  TaskRescheduleResponse,
  TaskWithProgress,
} from './task';
export type { FollowUpStatus, FollowUp, CreateFollowUpRequest, UpdateFollowUpRequest } from './followUp';
export type { FinancialExecution, CreateFinancialExecutionRequest, UpdateFinancialExecutionRequest } from './financial';
export type { InstrumentType, InstrumentStatus, PlanningInstrument, CreatePlanningInstrumentRequest, UpdatePlanningInstrumentRequest } from './planning';
export type { MeasurementUnit, PublicInvestmentProgram, ThematicCategory, IndicatorType, ManagementIndicator } from './catalog';
export type { PacMatrixResponse, PacInfo, StrategicLineReport, ProgramReport, ProjectReport, ActivityReport } from './report';
export type { ActivityTaskGroup, TaskDataMap, GanttTask } from './gantt';
export type { DashboardResponse, DashboardScope, DashboardTotals, LineProgress, ProgramProgress, TaskAlert, TaskSignal } from './report';
export type { TreeNode, LineNode, ProgramNodeDetail, ProjectNodeDetail, ActivityNodeDetail } from './treeNode';

// Backward-compatible aliases for existing consumer code
export type { UserResponse as AppUser } from './user';
export type { CreateUserRequest as CreateUserPayload } from './user';
export type { Project as ProjectItem } from './structure';
export type { Activity as ActivityItem } from './structure';
export type { Task as TaskItem } from './task';

/** @deprecated Use ApiError instead */
export interface ApiErrorBody {
  message: string;
}
