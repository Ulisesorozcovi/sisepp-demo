import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { FinancialTable } from '../design-system/organisms/FinancialTable';
import { FinancialFormModal } from '../design-system/organisms/FinancialFormModal';
import type { FinancialExecution } from '../types';

export function FinancialPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<FinancialExecution | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  function handleEdit(record: FinancialExecution) {
    setEditing(record);
    setModalOpen(true);
  }

  function handleClose() {
    setEditing(null);
    setModalOpen(false);
  }

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Ejecución financiera</h1>
          <p>Registra y consulta la ejecución presupuestal de los proyectos.</p>
        </div>
      </div>

      <FinancialTable
        refreshKey={refreshKey}
        onAdd={() => setModalOpen(true)}
        onEdit={handleEdit}
      />

      <FinancialFormModal
        open={modalOpen}
        editing={editing}
        onClose={handleClose}
        onSaved={() => setRefreshKey((k) => k + 1)}
      />
    </DashboardLayout>
  );
}
