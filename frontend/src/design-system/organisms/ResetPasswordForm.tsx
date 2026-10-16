import { useState, type FormEvent } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { PasswordField } from '../molecules/PasswordField';
import { Alert } from '../molecules/Alert';
import { Button } from '../atoms/Button';
import { authApi } from '../../api/authApi';

export function ResetPasswordForm() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  if (!token) {
    return (
      <div className="login-form-card">
        <p className="eyebrow">Error</p>
        <h2>Enlace inválido</h2>
        <p style={{ color: 'var(--neutral-600)', lineHeight: 1.6 }}>
          El enlace de restablecimiento no es válido. Solicita uno nuevo.
        </p>
        <Link to="/forgot-password">Solicitar nuevo enlace</Link>
      </div>
    );
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!password || !confirm) {
      setError('Completa ambos campos.');
      return;
    }
    if (password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (password !== confirm) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    setLoading(true);
    try {
      await authApi.resetPassword(token!, password);
      setSuccess(true);
      setTimeout(() => navigate('/login'), 3000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'El enlace expiró o ya fue utilizado.');
    } finally {
      setLoading(false);
    }
  }

  if (success) {
    return (
      <div className="login-form-card">
        <p className="eyebrow">Recuperación de contraseña</p>
        <h2>Contraseña actualizada</h2>
        <p style={{ color: 'var(--neutral-600)', lineHeight: 1.6 }}>
          Tu contraseña se restableció correctamente. Serás redirigido al inicio de sesión...
        </p>
        <Link to="/login">Ir al inicio de sesión</Link>
      </div>
    );
  }

  return (
    <div className="login-form-card">
      <p className="eyebrow">Recuperación de contraseña</p>
      <h2>Nueva contraseña</h2>
      <p style={{ color: 'var(--neutral-600)', marginBottom: 16, lineHeight: 1.6 }}>
        Ingresa y confirma tu nueva contraseña.
      </p>

      {error && <Alert>{error}</Alert>}

      <form onSubmit={handleSubmit}>
        <PasswordField id="password" label="Nueva contraseña" value={password} onChange={setPassword} />
        <PasswordField id="confirm" label="Confirmar contraseña" value={confirm} onChange={setConfirm} />
        <Button type="submit" disabled={loading}>
          {loading ? 'Guardando...' : 'Restablecer contraseña'}
        </Button>
      </form>

      <p className="login-footer-link">
        <Link to="/login">← Volver al inicio de sesión</Link>
      </p>
    </div>
  );
}
