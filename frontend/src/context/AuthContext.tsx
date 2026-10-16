import { createContext, useContext, useState, type ReactNode } from 'react';
import { authApi } from '../api/authApi';
import { getApiErrorMessage } from '../utils/apiError';
import type { AuthUser, UserRole } from '../types';

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<UserRole>;
  logout: () => void;
  hasRole: (role: UserRole) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readStoredUser(): AuthUser | null {
  const raw = localStorage.getItem('sisepp_user');
  return raw ? (JSON.parse(raw) as AuthUser) : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(readStoredUser());

  async function login(email: string, password: string) {
    try {
      const response = await authApi.login(email, password);
      const authUser: AuthUser = { email: response.email, fullName: response.fullName, role: response.role };
      localStorage.setItem('sisepp_token', response.token);
      localStorage.setItem('sisepp_user', JSON.stringify(authUser));
      setUser(authUser);
      return authUser.role;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Usuario o contraseña incorrectos'));
    }
  }

  function logout() {
    localStorage.removeItem('sisepp_token');
    localStorage.removeItem('sisepp_user');
    setUser(null);
  }

  function hasRole(role: UserRole) {
    return user?.role === role;
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return ctx;
}
