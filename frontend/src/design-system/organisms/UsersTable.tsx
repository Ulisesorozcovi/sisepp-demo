import { useEffect, useState } from 'react';
import { userApi } from '../../api/userApi';
import { getApiErrorMessage } from '../../utils/apiError';
import { UserCell } from '../molecules/UserCell';
import { Badge } from '../atoms/Badge';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import type { AppUser, UserRole } from '../../types';

const ROLE_LABEL: Record<UserRole, string> = {
  DIRECTOR: 'Director',
  COORDINADOR: 'Coordinador',
  STAFF: 'Staff',
  TECNICO: 'Técnico',
};

const ROLE_TONE: Record<UserRole, 'director' | 'coordinador' | 'staff' | 'tecnico'> = {
  DIRECTOR: 'director',
  COORDINADOR: 'coordinador',
  STAFF: 'staff',
  TECNICO: 'tecnico',
};

interface Props {
  refreshKey: number;
}

export function UsersTable({ refreshKey }: Props) {
  const [users, setUsers] = useState<AppUser[]>([]);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    userApi
      .list({ search: search || undefined, role: (roleFilter as UserRole) || undefined })
      .then((data) => active && setUsers(data))
      .catch((err) => active && setError(getApiErrorMessage(err, 'No se pudo cargar el listado de usuarios')))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [search, roleFilter, refreshKey]);

  async function handleInactivate(id: string) {
    if (!confirm('¿Inactivar este usuario? Podrá reactivarse más adelante.')) return;
    try {
      const updated = await userApi.inactivate(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? updated : u)));
    } catch (err) {
      alert(getApiErrorMessage(err, 'No se pudo inactivar el usuario'));
    }
  }

  async function handleActivate(id: string) {
    try {
      const updated = await userApi.activate(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? updated : u)));
    } catch (err) {
      alert(getApiErrorMessage(err, 'No se pudo activar el usuario'));
    }
  }

  return (
    <div className="organism-table-card">
      <div className="organism-table-toolbar">
        <Input
          placeholder="Buscar por nombre o correo"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <Select value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)} style={{ maxWidth: 180 }}>
          <option value="">Todos los roles</option>
          <option value="DIRECTOR">Director</option>
          <option value="COORDINADOR">Coordinador</option>
          <option value="STAFF">Staff</option>
          <option value="TECNICO">Técnico</option>
        </Select>
        <div className="spacer" />
      </div>

      {loading && <div className="empty-state">Cargando usuarios...</div>}
      {error && <div className="empty-state">{error}</div>}

      {!loading && !error && (
        <table className="users-table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Rol</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 && (
              <tr>
                <td colSpan={4} className="empty-state">
                  No hay usuarios que coincidan con la búsqueda.
                </td>
              </tr>
            )}
            {users.map((u) => (
              <tr key={u.id}>
                <td>
                  <UserCell fullName={u.fullName} email={u.email} />
                </td>
                <td>
                  <Badge tone={ROLE_TONE[u.role] ?? 'coordinador'}>
                    {ROLE_LABEL[u.role] ?? u.role}
                  </Badge>
                </td>
                <td>
                  <Badge tone={u.status === 'ACTIVO' ? 'activo' : 'inactivo'}>
                    {u.status === 'ACTIVO' ? 'Activo' : 'Inactivo'}
                  </Badge>
                </td>
                <td>
                  <div className="row-actions">
                    {u.status === 'ACTIVO' && u.role !== 'DIRECTOR' && (
                      <button className="danger" onClick={() => handleInactivate(u.id)}>
                        Inactivar
                      </button>
                    )}
                    {u.status === 'INACTIVO' && (
                      <button onClick={() => handleActivate(u.id)}>Activar</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
