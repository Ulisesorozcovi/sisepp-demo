import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { TasksTable } from '../design-system/organisms/TasksTable';
import { TaskFormModal } from '../design-system/organisms/TaskFormModal';
import { RescheduleModal } from '../design-system/organisms/RescheduleModal';
import { Button } from '../design-system/atoms/Button';
import type { Task } from '../types';

export function TasksPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [taskToReschedule, setTaskToReschedule] = useState<Task | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Cronograma de Tareas</h1>
          <p>Programa y reprograma tareas de tus proyectos, con meta física, fecha y responsable.</p>
        </div>
        <Button onClick={() => setModalOpen(true)}>+ Programar tarea</Button>
      </div>

      <TasksTable refreshKey={refreshKey} onReschedule={setTaskToReschedule} />

      <TaskFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={() => setRefreshKey((k) => k + 1)}
      />

      <RescheduleModal
        task={taskToReschedule}
        onClose={() => setTaskToReschedule(null)}
        onRescheduled={() => setRefreshKey((k) => k + 1)}
      />
    </DashboardLayout>
  );
}
