import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { FollowUpsTable } from '../design-system/organisms/FollowUpsTable';
import { FollowUpFormModal } from '../design-system/organisms/FollowUpFormModal';
import { useAuth } from '../context/AuthContext';
import type { FollowUp, UserRole } from '../types';

/**
 * Roles que pueden modificar un reporte de avance. Coincide con el
 * @PreAuthorize de PUT /follow-ups/{id}: el director supervisa y no reporta,
 * por eso no está en la lista.
 */
const EDIT_ROLES: UserRole[] = ['STAFF', 'COORDINADOR', 'TECNICO'];

export function FollowUpsPage() {
  const { user } = useAuth();
  const [refreshKey, setRefreshKey] = useState(0);
  const [editando, setEditando] = useState<FollowUp | null>(null);

  const puedeEditar = !!user && EDIT_ROLES.includes(user.role);

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Seguimiento a tareas</h1>
          <p>Supervisa el avance físico de las tareas y revisa las evidencias reportadas.</p>
        </div>
      </div>

      <FollowUpsTable
        refreshKey={refreshKey}
        puedeEditar={puedeEditar}
        onEdit={setEditando}
      />

      <FollowUpFormModal
        followUp={editando}
        onClose={() => setEditando(null)}
        onCreated={() => setRefreshKey((n) => n + 1)}
      />
    </DashboardLayout>
  );
}
