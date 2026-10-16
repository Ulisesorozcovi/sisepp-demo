import { useState, type FormEvent } from 'react';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';
import { Alert } from '../molecules/Alert';
import { userApi } from '../../api/userApi';
import { getApiErrorMessage } from '../../utils/apiError';
import type { UserRole } from '../../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onCreated: () => void;
}

/**
 * HU-02: creación de usuarios (Coordinador o Director), con validación de correo duplicado.
 */
export function UserFormModal({ open, onClose, onCreated }: Props) {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [role, setRole] = useState<UserRole>('COORDINADOR');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  if (!open) return null;

  function resetForm() {
    setFullName('');
    setEmail('');
    setRole('COORDINADOR');
    setError(null);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSaving(true);
    try {
      await userApi.create({ fullName, email, role });
      resetForm();
      onCreated();
      onClose();
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo crear el usuario'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <h3>Nuevo usuario</h3>

        {error && <Alert>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          <div className="molecule-field">
            <Label htmlFor="fullName">Nombre completo</Label>
            <Input
              id="fullName"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Nombre y apellido"
              required
            />
          </div>

          <div className="molecule-field">
            <Label htmlFor="email">Correo institucional</Label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="nombre.apellido@example.com"
              required
            />
          </div>

          <div className="molecule-field" style={{ marginBottom: 6 }}>
            <Label htmlFor="role">Rol</Label>
            <select
              id="role"
              className="atom-input"
              value={role}
              onChange={(e) => setRole(e.target.value as UserRole)}
            >
              <option value="COORDINADOR">Coordinador de Proyecto</option>
              <option value="DIRECTOR">Director</option>
              <option value="STAFF">Staff</option>
              <option value="TECNICO">Técnico</option>
            </select>
          </div>

          <div className="modal-actions">
            <Button type="button" variant="ghost" onClick={() => { resetForm(); onClose(); }}>
              Cancelar
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Guardando...' : 'Guardar'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
