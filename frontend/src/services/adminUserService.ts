import { apiRequest } from '@/api/http'
import type { Role } from '@/services/authService'

export interface UserAccount {
  id: number
  username: string
  role: Role
  enabled: boolean
}

export interface CreateUserAccountPayload {
  username: string
  password: string
  role: Role
}

export interface UpdateUserAccountPayload {
  role: Role
  enabled: boolean
}

export function findAllUsers(): Promise<UserAccount[]> {
  return apiRequest<UserAccount[]>('/api/admin/users')
}

export function findUserById(userId: number): Promise<UserAccount> {
  return apiRequest<UserAccount>(`/api/admin/users/${userId}`)
}

export function createUser(payload: CreateUserAccountPayload): Promise<UserAccount> {
  return apiRequest<UserAccount>('/api/admin/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function updateUser(
  userId: number,
  payload: UpdateUserAccountPayload,
): Promise<UserAccount> {
  return apiRequest<UserAccount>(`/api/admin/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function changeUserPassword(userId: number, password: string): Promise<UserAccount> {
  return apiRequest<UserAccount>(`/api/admin/users/${userId}/password`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      password,
    }),
  })
}
