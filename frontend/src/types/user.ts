import type { UserRole, UserStatus } from './auth';

export interface UserResponse {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
}

export interface CreateUserRequest {
  fullName: string;
  email: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  fullName: string;
  role: UserRole;
}
