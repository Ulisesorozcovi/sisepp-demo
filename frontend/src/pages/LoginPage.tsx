import { AuthLayout } from '../design-system/templates/AuthLayout';
import { LoginForm } from '../design-system/organisms/LoginForm';

export function LoginPage() {
  return (
    <AuthLayout>
      <LoginForm />
    </AuthLayout>
  );
}
