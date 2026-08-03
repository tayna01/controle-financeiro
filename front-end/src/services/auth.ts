const TOKEN_KEY = 'token'
const USER_KEY = 'user'

export interface SessionUser {
  nome: string
  email: string
}

interface MockUser {
  email: string
  senha: string
  nome: string
}

const MOCK_USERS: MockUser[] = [
  { email: 'usuario@exemplo.com', senha: '123456', nome: 'Tayná Vicente' },
  { email: 'teste@exemplo.com', senha: '123456', nome: 'Teste' },
]

export function login(email: string, senha: string): Promise<SessionUser> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = MOCK_USERS.find(
        (mock) => mock.email === email.toLowerCase() && mock.senha === senha,
      )

      if (!user) {
        reject(new Error('E-mail ou senha inválidos'))
        return
      }

      const sessionUser: SessionUser = { nome: user.nome, email: user.email }
      localStorage.setItem(TOKEN_KEY, `mock-token-${user.email}`)
      localStorage.setItem(USER_KEY, JSON.stringify(sessionUser))
      resolve(sessionUser)
    }, 1200)
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

export function changePassword(
  currentPassword: string,
  newPassword: string,
): Promise<void> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const sessionUser = getCurrentUser()

      if (!sessionUser) {
        reject(new Error('Sessão inválida. Faça login novamente.'))
        return
      }

      const user = MOCK_USERS.find((mock) => mock.email === sessionUser.email)

      if (!user || user.senha !== currentPassword) {
        reject(new Error('A senha atual está incorreta'))
        return
      }

      user.senha = newPassword
      resolve()
    }, 1200)
  })
}
