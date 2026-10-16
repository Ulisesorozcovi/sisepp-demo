import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { MyTasksList } from '../design-system/organisms/MyTasksList';
import { FollowUpFormModal } from '../design-system/organisms/FollowUpFormModal';
import type { Task } from '../types';

export function MyTasksPage() {
  const [taskForFollowUp, setTaskForFollowUp] = useState<Task | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Mis tareas</h1>
          <p>Consulta las tareas asignadas y registra el avance físico.</p>
        </div>
      </div>

      <MyTasksList
        refreshKey={refreshKey}
        onRegisterFollowUp={setTaskForFollowUp}
      />

      <FollowUpFormModal
        task={taskForFollowUp}
        onClose={() => setTaskForFollowUp(null)}
        onCreated={() => setRefreshKey((k) => k + 1)}
      />
    </DashboardLayout>
  );
}
