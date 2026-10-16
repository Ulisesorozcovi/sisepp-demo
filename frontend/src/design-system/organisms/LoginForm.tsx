import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FormField } from '../molecules/FormField';
import { PasswordField } from '../molecules/PasswordField';
import { Alert } from '../molecules/Alert';
import { Button } from '../atoms/Button';
import { useAuth } from '../../context/AuthContext';
import { HOME_BY_ROLE } from '../../routes/roles';

/**
 * HU-01: Inicio de sesión y control de acceso por rol.
 * Valida campos vacíos, credenciales incorrectas y cuenta bloqueada/inactiva
 * mostrando el mensaje que devuelve el backend en cada caso.
 */
export function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!email || !password) {
      setError('Ingresa tu correo y contraseña.');
      return;
    }

    setLoading(true);
    try {
      const loggedInRole = await login(email, password);
      navigate(HOME_BY_ROLE[loggedInRole]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Usuario o contraseña incorrectos');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-form-card">
      <p className="eyebrow">Acceso al sistema</p>
      <h2>Inicia sesión</h2>

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
        <PasswordField id="password" label="Contraseña" value={password} onChange={setPassword} />
        <Button type="submit" disabled={loading}>
          {loading ? 'Ingresando...' : 'Iniciar sesión'}
        </Button>
      </form>

      <p className="login-footer-link">
        ¿Olvidaste tu contraseña? <Link to="/forgot-password">Recupérala aquí</Link>
      </p>
    </div>
  );
}
