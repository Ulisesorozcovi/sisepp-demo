export type UserRole = 'DIRECTOR' | 'COORDINADOR' | 'STAFF' | 'TECNICO';
export type UserStatus = 'ACTIVO' | 'INACTIVO';

export interface AuthUser {
  email: string;
  fullName: string;
  role: UserRole;
}

export interface LoginResponse {
  token: string;
  fullName: string;
  role: UserRole;
  email: string;
}
