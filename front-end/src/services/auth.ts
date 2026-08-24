import { api, getApiErrorMessage } from '@/lib/api'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

export interface SessionUser {
  nome: string
  email: string
}

interface UserResponse {
  id: number
  name: string
  email: string
  createdAt: string
}

interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
}

function toSessionUser(response: UserResponse): SessionUser {
  return { nome: response.name, email: response.email }
}

export async function login(email: string, senha: string): Promise<SessionUser> {
  try {
    const { data } = await api.post<AuthResponse>('/auth/login', {
      email,
      password: senha,
    })
    localStorage.setItem(TOKEN_KEY, data.accessToken)

    const profile = await api.get<UserResponse>('/api/v1/users/me')
    const sessionUser = toSessionUser(profile.data)
    localStorage.setItem(USER_KEY, JSON.stringify(sessionUser))
    return sessionUser
  } catch (error) {
    throw new Error(getApiErrorMessage(error, 'E-mail ou senha inválidos'), {
      cause: error,
    })
  }
}

export async function register(nome: string, email: string, senha: string): Promise<void> {
  await api.post<UserResponse>('/auth/register', {
    name: nome,
    email,
    password: senha,
  })
}

export function isAuthenticated(): boolean {
  return Boolean(localStorage.getItem(TOKEN_KEY))
}

export function getCurrentUser(): SessionUser | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as SessionUser
  } catch {
    return null
  }
}

export function logout(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export async function changePassword(
  currentPassword: string,
  newPassword: string,
): Promise<void> {
  try {
    await api.patch('/api/v1/users/me/password', {
      currentPassword,
      newPassword,
    })
  } catch (error) {
    throw new Error(getApiErrorMessage(error, 'Falha ao alterar senha'), {
      cause: error,
    })
  }
}

export async function forgotPassword(
  email: string,
): Promise<{ message: string; debugToken?: string }> {
  const { data } = await api.post<{
    message: string
    debugToken?: string
  }>('/auth/forgot-password', { email })
  return data
}

export async function resetPassword(token: string, newPassword: string): Promise<string> {
  const { data } = await api.post<{ message: string }>('/auth/reset-password', {
    token,
    newPassword,
  })
  return data.message
}
