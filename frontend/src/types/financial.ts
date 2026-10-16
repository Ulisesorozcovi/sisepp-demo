export interface FinancialExecution {
  id: string;
  projectId: string;
  periodMonth: number;
  periodYear: number;
  approvedBudget: number;
  commitments: number;
  obligations: number;
  payments: number;
  availableBalance: number;
  executionPercentage: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFinancialExecutionRequest {
  projectId: string;
  periodMonth: number;
  periodYear: number;
  approvedBudget: number;
  commitments?: number;
  obligations?: number;
  payments?: number;
}

export interface UpdateFinancialExecutionRequest {
  approvedBudget?: number;
  commitments?: number;
  obligations?: number;
  payments?: number;
}
