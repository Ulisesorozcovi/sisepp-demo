import { useState } from 'react';
import { DashboardLayout } from '../design-system/templates/DashboardLayout';
import { UsersTable } from '../design-system/organisms/UsersTable';
import { UserFormModal } from '../design-system/organisms/UserFormModal';
import { Button } from '../design-system/atoms/Button';

export function UsersPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <DashboardLayout>
      <div className="dashboard-header">
        <div>
          <h1>Gestión de usuarios</h1>
          <p>Crea, edita e inactiva las cuentas de los usuarios del sistema.</p>
        </div>
        <Button onClick={() => setModalOpen(true)}>+ Nuevo usuario</Button>
      </div>

      <UsersTable refreshKey={refreshKey} />

      <UserFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={() => setRefreshKey((k) => k + 1)}
      />
    </DashboardLayout>
  );
}
