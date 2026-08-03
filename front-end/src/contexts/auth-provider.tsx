import { useState, type ReactNode } from 'react'
import { AuthContext } from '@/contexts/auth-context'
import {
  getCurrentUser,
  isAuthenticated as hasStoredSession,
  login as authenticate,
  logout as clearSession,
} from '@/services/auth'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState(() => getCurrentUser())
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() =>
    hasStoredSession(),
  )

  async function login(email: string, senha: string) {
    const loggedUser = await authenticate(email, senha)
    setUser(loggedUser)
    setIsAuthenticated(true)
  }

  function logout() {
    clearSession()
    setUser(null)
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
