import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { FormField } from '../molecules/FormField';
import { Alert } from '../molecules/Alert';
import { Button } from '../atoms/Button';
import { authApi } from '../../api/authApi';

export function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!email) {
      setError('Ingresa tu correo institucional.');
      return;
    }

    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      setSent(true);
    } catch {
      setSent(true);
    } finally {
      setLoading(false);
    }
  }

  if (sent) {
    return (
      <div className="login-form-card">
        <p className="eyebrow">Recuperación de contraseña</p>
        <h2>Revisa tu correo</h2>
        <p style={{ color: 'var(--neutral-600)', lineHeight: 1.6 }}>
          Si existe una cuenta asociada a <strong>{email}</strong>, recibirás un enlace para restablecer tu contraseña.
        </p>
        <Link to="/login" style={{ marginTop: 16, display: 'inline-block' }}>
          ← Volver al inicio de sesión
        </Link>
      </div>
    );
  }

  return (
    <div className="login-form-card">
      <p className="eyebrow">Recuperación de contraseña</p>
      <h2>¿Olvidaste tu contraseña?</h2>
      <p style={{ color: 'var(--neutral-600)', marginBottom: 16, lineHeight: 1.6 }}>
        Ingresa tu correo institucional y te enviaremos un enlace para restablecerla.
      </p>

      {error && <Alert>{error}</Alert>}

      <form onSubmit={handleSubmit}>
        <FormField
          id="email"
          label="Correo institucional"
          type="email"
          placeholder="nombre.apellido@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <Button type="submit" disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar enlace'}
        </Button>
      </form>

      <p className="login-footer-link">
        <Link to="/login">← Volver al inicio de sesión</Link>
      </p>
    </div>
  );
}
