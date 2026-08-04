import { apiRequest, clearCsrfToken, loadCsrfToken } from '@/api/http'

export type Role = 'ADMIN' | 'OPERATOR'

export interface CurrentUser {
  username: string
  role: Role
}

export interface LoginCredentials {
  username: string
  password: string
}

export async function login(credentials: LoginCredentials): Promise<CurrentUser> {
  const user = await apiRequest<CurrentUser>('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  })

  /*
   * Se cambia sessione e token CSRF si premde il nuovo token associato.
   */
  clearCsrfToken()
  await loadCsrfToken()

  return user
}

export function currentUser(): Promise<CurrentUser> {
  return apiRequest<CurrentUser>('/api/auth/me')
}
